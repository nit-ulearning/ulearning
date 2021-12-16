package org.ulearn.smstemplateservice.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.ulearn.smstemplateservice.entity.GlobalEntity;
import org.ulearn.smstemplateservice.entity.GlobalResponseEntity;
import org.ulearn.smstemplateservice.entity.SmsTemplateEntity;
import org.ulearn.smstemplateservice.exception.CustomException;
import org.ulearn.smstemplateservice.repository.SmsTemplateRepo;
import org.ulearn.smstemplateservice.validation.FieldValidation;

@Service
public class SmsTemplateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsTemplateService.class);

	@Autowired
	private SmsTemplateRepo smsTemplateRepo;

	@Autowired
	private FieldValidation fieldValidation;

	public GlobalResponseEntity addSmsTemplate(GlobalEntity globalEntity) {
		LOGGER.info("Inside - SmsTemplateService.addSmsTemplate()");
		try {

			if (validateData(globalEntity)) {
				Optional<SmsTemplateEntity> findByStName = smsTemplateRepo.findByStName(globalEntity.getStName());
				if (findByStName.isPresent()) {
					throw new CustomException("Template Name Already Exists");
				}
				this.checkTag(globalEntity);
				SmsTemplateEntity smsTemplateEntity = new SmsTemplateEntity();
				smsTemplateEntity.setIsActive(1);
				smsTemplateEntity.setIsDeleted(0);
				if (globalEntity.getIsPrimary() == 1) {
					List<SmsTemplateEntity> items = new ArrayList<SmsTemplateEntity>();
					List<SmsTemplateEntity> findAll = smsTemplateRepo.findByStAction(globalEntity.getStAction());
					findAll.forEach(item -> {
						item.setIsPrimary(0);
						items.add(item);
					});
					smsTemplateRepo.saveAll(items);
					smsTemplateEntity.setIsPrimary(globalEntity.getIsPrimary());
				} else {
					smsTemplateEntity.setIsPrimary(globalEntity.getIsPrimary());
				}
				smsTemplateEntity.setStAction(globalEntity.getStAction());
				smsTemplateEntity.setCreatedOn(new Date());
				smsTemplateEntity.setStBody(globalEntity.getStBody());
				smsTemplateEntity.setStName(globalEntity.getStName());
				smsTemplateEntity.setStOrder(globalEntity.getStOrder());
				// need to discuss
				smsTemplateEntity.setStTags(globalEntity.getStTags());
				//// End
				smsTemplateEntity.setStTempId(globalEntity.getStTempId());
				smsTemplateEntity.setStSubject(globalEntity.getStSubject());
				smsTemplateEntity.setStType("CUSTOM");
				SmsTemplateEntity save = smsTemplateRepo.save(smsTemplateEntity);
				if (!save.equals(null)) {
					return new GlobalResponseEntity("SUCCESS", "SMS Template Added Successfully", 200);
				} else {
					throw new CustomException("Internal Server Error");
				}

			} else {
				throw new CustomException("Validation Error");
			}

		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	public Map<String, Object> getSmsTemplate(int delete,Optional<Integer> page, Optional<String> sortBy) {
		LOGGER.info("Inside - SmsTemplateService.getSmsTemplate()");
		try {
			int Limit = 10;

			Pageable pagingSort = PageRequest.of(page.orElse(0), Limit, Sort.Direction.DESC,
					sortBy.orElse("createdOn"));
			Page<SmsTemplateEntity> findAll = smsTemplateRepo.findAllAndDelete(delete, pagingSort);

			int totalPage = findAll.getTotalPages() - 1;
			if (totalPage < 0) {
				totalPage = 0;
			}
			Map<String, Object> response = new HashMap<>();
			response.put("data", findAll.getContent());
			response.put("currentPage", findAll.getNumber());
			response.put("total", findAll.getTotalElements());
			response.put("totalPage", totalPage);
			response.put("perPage", findAll.getSize());
			response.put("perPageElement", findAll.getNumberOfElements());

			if (findAll.getSize() < 1) {
				throw new CustomException("Institute Not Found!");
			} else {
				return response;
			}

//			List<SmsTemplateEntity> findAll = smsTemplateRepo.findTemplate(0);
//			if (findAll.size() > 0) {
//				return findAll;
//			} else {
//				throw new CustomException("Data not found");
//			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	public GlobalResponseEntity updateSmsTemplate(Long stId, GlobalEntity globalEntity) {
		LOGGER.info("Inside - SmsTemplateService.updateSmsTemplate()");
		try {
			Optional<SmsTemplateEntity> findById = smsTemplateRepo.findById(stId);
			if (findById.isPresent()) {
				if (validateData(globalEntity)) {
					Optional<SmsTemplateEntity> findByStName = smsTemplateRepo.findByStName(globalEntity.getStName());
					if (findByStName.isPresent() && findByStName.get().getStId() != stId) {
						throw new CustomException("Template Name Already Exists");
					}
					this.checkTag(globalEntity);
					SmsTemplateEntity smsTemplateEntity = new SmsTemplateEntity();
					smsTemplateEntity.setIsActive(findById.get().getIsActive());
					smsTemplateEntity.setIsDeleted(findById.get().getIsDeleted());

					if (globalEntity.getIsPrimary() == 1) {
						List<SmsTemplateEntity> items = new ArrayList<SmsTemplateEntity>();
						List<SmsTemplateEntity> findAll = smsTemplateRepo.findByStAction(globalEntity.getStAction());
						findAll.forEach(item -> {
							item.setIsPrimary(0);
							items.add(item);
						});
						smsTemplateRepo.saveAll(items);
						smsTemplateEntity.setIsPrimary(globalEntity.getIsPrimary());
					} else {
						smsTemplateEntity.setIsPrimary(globalEntity.getIsPrimary());
					}

//					smsTemplateEntity.setIsPrimary(globalEntity.getIsPrimary());
					smsTemplateEntity.setStAction(globalEntity.getStAction());
					smsTemplateEntity.setCreatedOn(findById.get().getCreatedOn());
					smsTemplateEntity.setStBody(globalEntity.getStBody());
					smsTemplateEntity.setStSubject(globalEntity.getStSubject());
					smsTemplateEntity.setStName(globalEntity.getStName());
					smsTemplateEntity.setStOrder(findById.get().getStOrder());
					smsTemplateEntity.setStTags(findById.get().getStTags());
					smsTemplateEntity.setStTempId(globalEntity.getStTempId());
					smsTemplateEntity.setStType(findById.get().getStType());
					smsTemplateEntity.setUpdatedOn(new Date());
					smsTemplateEntity.setStId(stId);
					SmsTemplateEntity save = smsTemplateRepo.save(smsTemplateEntity);
					if (!save.equals(null)) {
						return new GlobalResponseEntity("SUCCESS", "SMS Template Updated Successfully", 200);
					} else {
						throw new CustomException("Data Not Save");
					}
				} else {
					throw new CustomException("Validation Error");
				}
			} else {
				throw new CustomException("Data Not Found For Your Request");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	private boolean validateData(GlobalEntity globalEntity) {

		if (!fieldValidation.isEmpty(globalEntity.getStName())) {
			throw new CustomException("Template Name Required");
		} 
//		else if (!fieldValidation.isEmpty(globalEntity.getStSubject())) {
//			// SMS template Subject represent SMS Header
//			throw new CustomException("SMS template header required");
//		} 
		else if (!fieldValidation.isEmpty(globalEntity.getStBody())) {
			// SMS template Body represent SMS Content
			throw new CustomException("SMS Template Content Required");
		} else if (!fieldValidation.isEmpty(globalEntity.getStAction())) {
			// SMS StAction represent Template For
			throw new CustomException("Template For Required");
		} else if (!fieldValidation.isEmpty(globalEntity.getIsPrimary())) {
			// SMS template is_Primary represent Set Default
			LOGGER.info(globalEntity.getIsPrimary() + "");
			throw new CustomException("Set Default Required");
		} else {
			return true;
		}

	}

	public GlobalResponseEntity deleteSmsTemplate(Long stId) {
		LOGGER.info("Inside - SmsTemplateService.deleteSmsTemplate()");
		try {
			Optional<SmsTemplateEntity> findById = smsTemplateRepo.findById(stId);
			if (findById.isPresent()) {
				if (findById.get().getIsDeleted() == 0) {
					if (findById.get().getIsPrimary() == 1) {
						throw new CustomException("Primary Template Can't Be Deleted");
					}
					SmsTemplateEntity smsTemplateEntity = new SmsTemplateEntity();
					smsTemplateEntity.setCreatedOn(findById.get().getCreatedOn());
					smsTemplateEntity.setIsActive(findById.get().getIsActive());
					smsTemplateEntity.setIsDeleted(1);
					smsTemplateEntity.setIsPrimary(0);
					smsTemplateEntity.setStAction(findById.get().getStAction());
					smsTemplateEntity.setStBody(findById.get().getStBody());
					smsTemplateEntity.setStId(stId);
					smsTemplateEntity.setStName(findById.get().getStName());
					smsTemplateEntity.setStOrder(findById.get().getStOrder());
					smsTemplateEntity.setStSubject(findById.get().getStSubject());
					smsTemplateEntity.setStTags(findById.get().getStTags());
					smsTemplateEntity.setStTempId(findById.get().getStTempId());
					smsTemplateEntity.setStType(findById.get().getStType());
					smsTemplateEntity.setUpdatedOn(new Date());
					SmsTemplateEntity save = smsTemplateRepo.save(smsTemplateEntity);
					if (!save.equals(null)) {
						return new GlobalResponseEntity("SUCCESS", "SMS Template Deleted Successfully", 200);
					} else {
						throw new CustomException("Internal Server Error");
					}
				} else {
					throw new CustomException("Deleted Template Can't  Delete Again");
				}
			} else {
				throw new CustomException("Your Requested Id Is Not Available");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	public SmsTemplateEntity getSmsTemplateById(Long stId) {
		LOGGER.info("Inside - SmsTemplateService.deleteSmsTemplate()");
		try {
			Optional<SmsTemplateEntity> findById = smsTemplateRepo.findById(stId);
			if (findById.isPresent()) {
				return findById.get();
			} else {
				throw new CustomException("Data Not Found");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	public List<SmsTemplateEntity> getSmsTemplateForDropDown() {
		LOGGER.info("Inside - SmsTemplateService.deleteSmsTemplate()");
		try {
			List<SmsTemplateEntity> findByStType = smsTemplateRepo.findByStType("DEFAULT");
			if (findByStType.size() > 0) {
				return findByStType;
			} else {
				throw new CustomException("Data Not Found");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	public GlobalResponseEntity setDefaultSmsTemplate(Long stId, String stAction) {
		LOGGER.info("Inside - SmsTemplateService.setDefaultSmsTemplate()");
		try {
			Optional<SmsTemplateEntity> findById = smsTemplateRepo.findById(stId);
			if (findById.isPresent()) {
				SmsTemplateEntity smsTemplateEntity = new SmsTemplateEntity();
				smsTemplateEntity.setCreatedOn(findById.get().getCreatedOn());
				smsTemplateEntity.setIsActive(findById.get().getIsActive());
				smsTemplateEntity.setIsDeleted(findById.get().getIsDeleted());
				smsTemplateEntity.setIsPrimary(1);
				smsTemplateEntity.setStAction(findById.get().getStAction());
				smsTemplateEntity.setStBody(findById.get().getStBody());
				smsTemplateEntity.setStId(findById.get().getStId());
				smsTemplateEntity.setStName(findById.get().getStName());
				smsTemplateEntity.setStOrder(findById.get().getStOrder());
				smsTemplateEntity.setStSubject(findById.get().getStSubject());
				smsTemplateEntity.setStTags(findById.get().getStTags());
				smsTemplateEntity.setStTempId(findById.get().getStTempId());
				smsTemplateEntity.setStType(findById.get().getStType());
				smsTemplateEntity.setUpdatedOn(new Date());
				List<SmsTemplateEntity> findAll = smsTemplateRepo.findByStAction(stAction);
				List<SmsTemplateEntity> items = new ArrayList<SmsTemplateEntity>();
				findAll.forEach(item -> {
					item.setIsPrimary(0);
					items.add(item);
				});
				List<SmsTemplateEntity> saveAll = smsTemplateRepo.saveAll(items);
				if (saveAll.size() > 0) {
					SmsTemplateEntity save = smsTemplateRepo.save(smsTemplateEntity);
					if (!save.equals(null)) {
						return new GlobalResponseEntity("SUCCESS", "Tempate Set As Default", 200);
					} else {
						throw new CustomException("Can't Set This Template As Default");
					}
				} else {
					throw new CustomException("Not Deactiavte Other Template");
				}
			} else {
				throw new CustomException("Your Requested Id Is Not Available");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	public Map<String, Object> getSmsTemplatePagination(int page, int limit, String sort, String sortName,
			Optional<String> keyword, Optional<String> sortBy, int isDelete) {
		LOGGER.info("Inside - SmsTemplateService.getSmsTemplatePagination()");
		try {
			Pageable pagingSort = null;
			int CountData=(int) smsTemplateRepo.count();							
			if(limit==0) {
				limit=CountData;
			}
			if (sort.equals("ASC")) {
				pagingSort = PageRequest.of(page, limit, Sort.Direction.ASC, sortBy.orElse(sortName));
			} else {
				pagingSort = PageRequest.of(page, limit, Sort.Direction.DESC, sortBy.orElse(sortName));
			}

			Page<SmsTemplateEntity> findAll = null;
			if (keyword.isPresent()) {
				findAll = smsTemplateRepo.search(keyword.get(), isDelete, pagingSort);
			} else {
				findAll = smsTemplateRepo.findAllAndDelete(isDelete, pagingSort);
			}
			int totalPage = findAll.getTotalPages() - 1;
			if (totalPage < 0) {
				totalPage = 0;
			}

			Map<String, Object> response = new HashMap<>();
			response.put("data", findAll.getContent());
			response.put("currentPage", findAll.getNumber());
			response.put("total", findAll.getTotalElements());
			response.put("totalPage", totalPage);
			response.put("perPage", findAll.getSize());
			response.put("perPageElement", findAll.getNumberOfElements());

			if (findAll.getSize() < 1) {
				throw new CustomException("SMS Template Not Found!");
			} else {
				return response;
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	private void checkTag(GlobalEntity globalEntity) {

		List<SmsTemplateEntity> findByEtActionWithDefaultET = smsTemplateRepo
				.findByEtActionWithDefaultET(globalEntity.getStAction(), "DEFAULT");
		String[] split = globalEntity.getStBody().split("__");
		String tags = "";
		for (int i = 0; i < split.length; i++) {
			if (split[i].startsWith("$") && split[i].endsWith("$")) {
				LOGGER.info("tags: " + split[i]);
				tags = tags + split[i] + " ";
			}
		}
		if (findByEtActionWithDefaultET.size() < 1) {
			throw new CustomException("The Custome Template Action is Not Present in Default Action");
		}
		if (findByEtActionWithDefaultET.get(0).getStTags().split(",").length < tags.split(" ").length) {
			throw new CustomException("Tags lengths Are not Match");
		}
		String[] splitTags = tags.split(" ");
		String splitTagsFromDB = findByEtActionWithDefaultET.get(0).getStTags();
		for (int i = 0; i < splitTags.length; i++) {
			if (splitTagsFromDB.indexOf(splitTags[i]) == -1) {
				throw new CustomException("Tags Are Not Present in Default Template");
			}
		}
	}

	public SmsTemplateEntity getPrimaryETByAction(String action) {
		LOGGER.info("Inside - SmsTemplateService.getPrimaryETByAction()");
		try {
			List<SmsTemplateEntity> find = smsTemplateRepo.getPrimarySTByAction(action, 1);
			if (find.size() > 0) {
				return find.get(0);
			} else {
				throw new CustomException("SMS Template Not Found");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	public List<Map<String, String>> getTags(String action) {
		LOGGER.info("Inside - SmsTemplateService.getTags()");
		try {
			Optional<SmsTemplateEntity> findTagByActionAndType = smsTemplateRepo.findTagByActionAndType(action, "DEFAULT");
			if (findTagByActionAndType.isPresent()) {
				SmsTemplateEntity smsTemplateEntity = findTagByActionAndType.get();
				String stTags = smsTemplateEntity.getStTags();
				String[] split = stTags.split(",");
				
				String stTagsName = smsTemplateEntity.getStTagsName();
				String[] split2 = stTagsName.split(",");
				
				List<Map<String, String>> list = new ArrayList<>();
				
				for(int i=0; i < split.length; i++) {
					
					Map<String , String> map = new HashMap<String, String>();
					map.put("value", split[i]);
					map.put("name", split2[i]);
					list.add(map);
				}
				return list;
			} else {
				throw new CustomException("Data Not Present");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
		
	}

	public GlobalResponseEntity smsTemplateRestore(Long id) {
		LOGGER.info("Inside - SmsTemplateService.getTags()");
		try {
			Optional<SmsTemplateEntity> findById = smsTemplateRepo.findById(id);
			SmsTemplateEntity smsTemplateEntity = findById.get();
			if (findById.isPresent()) {
				if (smsTemplateEntity.getIsDeleted() == 1) {
					if (smsTemplateEntity.getStType().equals("CUSTOM")) {
						smsTemplateEntity.setIsDeleted(0);
						LOGGER.info("final Data is --->  "+smsTemplateEntity.toString());
						SmsTemplateEntity save = smsTemplateRepo.save(smsTemplateEntity);
						if (!save.equals(null)) {
							return new GlobalResponseEntity("SUCCESS", "Template Restore Successfully", 200);
						} else {
							throw new CustomException("Data not Restored");
						}
					} else {
						throw new CustomException("You Can't Restore Default Template");
					}
				} else {
					throw new CustomException("Can't Restore an Omitted Template");
				}
			} else {
				throw new CustomException("Can't Find The Template");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	public GlobalResponseEntity changeIsprimary(Long stId) {
		LOGGER.info("Inside - SmsTemplateService.changeIsprimary()");
		try {
			Optional<SmsTemplateEntity> findById = smsTemplateRepo.findById(stId);
			SmsTemplateEntity smsTemplateEntity = findById.get();
			if (findById.isPresent()) {
				if (smsTemplateEntity.getIsPrimary() == 0) {
					smsTemplateEntity.setIsPrimary(1);
					SmsTemplateEntity save = smsTemplateRepo.save(smsTemplateEntity);
					if (!save.equals(null)) {
						return new GlobalResponseEntity("SUCCESS", "Template Change As Primary", 200);
					} else {
						throw new CustomException("Can't Set this Template As Primary");
					}
				} else {
					smsTemplateEntity.setIsPrimary(0);
					SmsTemplateEntity save = smsTemplateRepo.save(smsTemplateEntity);
					if (!save.equals(null)) {
						return new GlobalResponseEntity("SUCCESS", "Template Change As Non Primary", 200);
					} else {
						throw new CustomException("Can't Set this Template As Non Primary");
					}
				}
			} else {
				throw new CustomException("Could Not Find Any Template");
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

}
