package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.dto.user.UserExtendedDto;
import dev.knalis.vleapi.model.entity.user.*;
import dev.knalis.vleapi.repo.AdminProfileRepo;
import dev.knalis.vleapi.repo.StudentProfileRepo;
import dev.knalis.vleapi.repo.TeacherProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProfileAssembler {

    @Autowired private StudentProfileRepo studentProfileRepo;
    @Autowired private TeacherProfileRepo teacherProfileRepo;
    @Autowired private AdminProfileRepo adminProfileRepo;

    public UserExtendedDto assemble(User user) {
        if (user == null) return null;
        UserExtendedDto dto = new UserExtendedDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole() == null ? null : user.getRole().name());
        dto.setFio(user.getFio());

        switch (user.getRole()) {
            case STUDENT -> studentProfileRepo.findByUserId(user.getId()).ifPresent(sp -> dto.setGroupId(sp.getGroup()==null?null:sp.getGroup().getId()));
            case TEACHER -> teacherProfileRepo.findByUserId(user.getId()).ifPresent(tp -> {
                dto.setAcademicTitle(tp.getAcademicTitle());
                dto.setDepartment(tp.getDepartment());
                dto.setWorkPhone(tp.getWorkPhone());
                dto.setScientificDegree(tp.getScientificDegree());
            });
            case ADMINISTRATOR -> adminProfileRepo.findByUserId(user.getId()).ifPresent(ap -> dto.setDepartment(ap.getDepartment()));
            default -> {}
        }
        return dto;
    }
}
