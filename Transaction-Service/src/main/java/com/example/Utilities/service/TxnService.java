package com.example.Utilities.service;

import com.example.Utilities.dtos.TxnInitiatedKafkaDTO;
import com.example.Utilities.dtos.UserSecurityDTO;
import com.example.Utilities.model.Txn;
import com.example.Utilities.model.TxnStatus;
import com.example.Utilities.repository.TxnRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Slf4j
@Service
public class TxnService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TxnRepository txnRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public String initTxn(String receiver, String purpose, String amount, String sender) {

        Txn txn = Txn.builder()
                .txnId(UUID.randomUUID().toString())
                .amount(Double.valueOf(amount))
                .receiver(receiver)
                .sender(sender)
                .purpose(purpose)
                .status(TxnStatus.INITIATED)
                .build();

        txnRepository.save(txn);

        // ✅ Build Kafka DTO
        TxnInitiatedKafkaDTO kafkaDTO = TxnInitiatedKafkaDTO.builder()
                .txnId(txn.getTxnId())
                .amount(txn.getAmount())
                .sender(txn.getSender())
                .receiver(txn.getReceiver())
                .status(txn.getStatus())
                .purpose(txn.getPurpose())
                .build();

        // ✅ Serialize DTO → JSON
        String message = objectMapper.writeValueAsString(kafkaDTO);

        // ✅ Publish to Kafka
        kafkaTemplate.send("TXN_INITIATED_TOPIC", message);

        return txn.getTxnId();
    }


    @Override
    public UserDetails loadUserByUsername(String userContact)
            throws UsernameNotFoundException {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth("txn-service", "txn-service");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<UserSecurityDTO> response =
                    restTemplate.exchange(
                            "http://localhost:8083/user/userDetails?contact=" + userContact,
                            HttpMethod.GET,
                            request,
                            UserSecurityDTO.class
                    );

            UserSecurityDTO dto = response.getBody();

            if (dto == null) {
                throw new UsernameNotFoundException("User not found: " + userContact);
            }

            List<GrantedAuthority> authorities =
                    dto.getAuthorities()
                            .stream()
                            .map(a -> (GrantedAuthority) new SimpleGrantedAuthority(a))
                            .toList();

            log.info("Authenticated user={}, authorities={}",
                    dto.getContact(), authorities);

            return new org.springframework.security.core.userdetails.User(
                    dto.getContact(),
                    dto.getPassword(),
                    dto.isEnabled(),
                    dto.isAccountNonExpired(),
                    dto.isCredentialsNonExpired(),
                    dto.isAccountNonLocked(),
                    authorities
            );

        } catch (HttpClientErrorException e) {
            // User-service returned 401/403
            log.error("User-service auth error: {}", e.getStatusCode());
            throw new UsernameNotFoundException("User-service authentication failed", e);

        } catch (RestClientException e) {
            // Network / deserialization / server error
            log.error("User-service communication error", e);
            throw new UsernameNotFoundException("User-service unavailable", e);
        }
    }


}
