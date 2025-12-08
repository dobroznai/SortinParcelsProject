package idhub.sortinparcels.service;


import idhub.sortinparcels.model.ParcelAudit;
import idhub.sortinparcels.repository.ParcelAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ParcelAuditRepository repository;

    public List<ParcelAudit> getAllEvents() {
        return repository.findAll();
    }

    public List<ParcelAudit> getEventsBySession(String sessionId) {
        return repository.findBySessionId(sessionId);
    }

    public List<ParcelAudit> getEventsByTrackingNumber(String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber);
    }
}