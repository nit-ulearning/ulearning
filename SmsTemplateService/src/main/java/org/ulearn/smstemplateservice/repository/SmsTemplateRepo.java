package org.ulearn.smstemplateservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ulearn.smstemplateservice.entity.SmsTemplateEntity;

public interface SmsTemplateRepo extends JpaRepository<SmsTemplateEntity, Long> {
	@Query(value = "select * from tbl_sms_templates where IS_DELETED = ?1", nativeQuery = true)
	List<SmsTemplateEntity> findTemplate(int isDeleted);
	
	List<SmsTemplateEntity> findByStType(String stType);
	
	List<SmsTemplateEntity> findByStAction(String stAction);

	@Query("SELECT Obj FROM SmsTemplateEntity Obj WHERE Obj.isDeleted = ?2 and Obj.stName LIKE %?1%")
	Page<SmsTemplateEntity> search(String string, int isDelete, Pageable pagingSort);
	
	Optional<SmsTemplateEntity> findByStName(String stName);

	@Query(value = "select obj from SmsTemplateEntity obj where obj.stAction = ?1 and obj.stType = ?2")
	List<SmsTemplateEntity> findByEtActionWithDefaultET(String stAction, String string);

	@Query(value = "select obj from SmsTemplateEntity obj where obj.stAction = ?1 and obj.isPrimary = ?2")
	List<SmsTemplateEntity> getPrimarySTByAction(String action, int i);

	@Query(value = "select obj from SmsTemplateEntity obj where obj.isDeleted = ?1")
	public Page<SmsTemplateEntity> findAllAndDelete(int delete, Pageable pagingSort);

	@Query(value = "select obj from SmsTemplateEntity obj where obj.stAction = ?1 and obj.stType = ?2")
	Optional<SmsTemplateEntity> findTagByActionAndType(String action, String type);
}
