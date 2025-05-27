package mz.org.csaude.comvida.backend.base;

import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.util.DateUtils;

public abstract class BaseService {
    protected void addCreationAuditInfo(BaseEntity entity, User user) {
        entity.setCreatedBy(user.getUuid());
        entity.setCreatedAt(DateUtils.getCurrentDate());
        entity.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.ACTIVE);
    }

    protected void addUpdateAuditInfo(BaseEntity newEntity, BaseEntity oldEntity, User user) {
        newEntity.setCreatedBy(oldEntity.getCreatedBy());
        newEntity.setCreatedAt(oldEntity.getCreatedAt());
        newEntity.setLifeCycleStatus(oldEntity.getLifeCycleStatus());
        newEntity.setUpdatedBy(user.getUuid());
        newEntity.setUpdatedAt(DateUtils.getCurrentDate());
    }
}
