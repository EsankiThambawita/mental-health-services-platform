package com.mentalhealth.recoveryplan.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import com.mentalhealth.recoveryplan.model.PlanStatus;
import com.mentalhealth.recoveryplan.model.RecoveryPlan;
import com.mentalhealth.recoveryplan.repository.RecoveryPlanRepository;

class FakeRecoveryPlanRepository implements RecoveryPlanRepository {

    private Map<String, RecoveryPlan> storage = new HashMap<>();

    @Override
    public Optional<RecoveryPlan> findByIdAndCounselorId(String id, String counselorId) {
        RecoveryPlan plan = storage.get(id);
        if (plan != null && plan.getCounselorId().equals(counselorId)) {
            return Optional.of(plan);
        }
        return Optional.empty();
    }

    @Override
    public Optional<RecoveryPlan> findByIdAndPatientId(String id, String patientId) {
        RecoveryPlan plan = storage.get(id);
        if (plan != null && plan.getPatientId().equals(patientId)) {
            return Optional.of(plan);
        }
        return Optional.empty();
    }

    @Override
    public List<RecoveryPlan> findByCounselorId(String counselorId) {
        return storage.values().stream()
                .filter(p -> p.getCounselorId().equals(counselorId))
                .toList();
    }

    @Override
    public List<RecoveryPlan> findByPatientId(String patientId) {
        return storage.values().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .toList();
    }

    @Override
    public <S extends RecoveryPlan> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public <S extends RecoveryPlan> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public <S extends RecoveryPlan> S insert(S entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public <S extends RecoveryPlan> List<S> insert(Iterable<S> entities) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public List<RecoveryPlan> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public List<RecoveryPlan> findAllById(Iterable<String> ids) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllById'");
    }

    @Override
    public <S extends RecoveryPlan> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveAll'");
    }

    @Override
    public long count() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'count'");
    }

    @Override
    public void delete(RecoveryPlan entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
    }

    @Override
    public void deleteAll(Iterable<? extends RecoveryPlan> entities) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllById'");
    }

    @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public boolean existsById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsById'");
    }

    @Override
    public Optional<RecoveryPlan> findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public <S extends RecoveryPlan> S save(S entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public List<RecoveryPlan> findAll(Sort sort) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public Page<RecoveryPlan> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public <S extends RecoveryPlan> long count(Example<S> example) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'count'");
    }

    @Override
    public <S extends RecoveryPlan> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exists'");
    }

    @Override
    public <S extends RecoveryPlan> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public <S extends RecoveryPlan, R> R findBy(Example<S> example,
            Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findBy'");
    }

    @Override
    public <S extends RecoveryPlan> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOne'");
    }

    @Override
    public List<RecoveryPlan> findByPatientIdAndStatus(String patientId, PlanStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByPatientIdAndStatus'");
    }

    @Override
    public boolean existsByPatientIdAndStatus(String patientId, PlanStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsByPatientIdAndStatus'");
    }
}