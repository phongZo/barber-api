package com.barber.api.unit.nation;

import com.barber.api.constant.BarberConstant;
import com.barber.api.controller.NationController;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.nation.NationAdminDto;
import com.barber.api.dto.nation.NationDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.form.nation.CreateNationForm;
import com.barber.api.form.nation.UpdateNationForm;
import com.barber.api.jwt.BarberJwt;
import com.barber.api.mapper.NationMapper;
import com.barber.api.model.Nation;
import com.barber.api.model.criteria.NationCriteria;
import com.barber.api.repository.BranchRepository;
import com.barber.api.repository.NationRepository;
import com.barber.api.service.impl.UserServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NationControllerTest {

  @InjectMocks
  NationController nationController;

  @Mock
  NationRepository nationRepository;

  @Mock
  BranchRepository branchRepository;

  @Spy
  NationMapper nationMapper = Mappers.getMapper(NationMapper.class);

  @Mock
  UserServiceImpl userService;

  @Mock
  BindingResult bindingResult;

  private static Nation mockProvince;

  private static Nation mockDistrict;

  private static Nation mockWard;

  @BeforeAll
  static void initData(){
    mockProvince = new Nation();
    mockProvince.setId(1L);
    mockProvince.setName("Bình Dương");
    mockProvince.setKind(BarberConstant.NATION_KIND_PROVINCE);

    mockDistrict = new Nation();
    mockDistrict.setId(2L);
    mockDistrict.setName("Thủ Dầu Một");
    mockDistrict.setKind(BarberConstant.NATION_KIND_DISTRICT);
    mockDistrict.setParent(mockProvince);

    mockWard = new Nation();
    mockWard.setId(3L);
    mockWard.setName("Tân Phú");
    mockWard.setKind(BarberConstant.NATION_KIND_WARD);
    mockWard.setParent(mockDistrict);
  }

  @Nested
  @DisplayName("Admin actions")
  class AdminTest{
    @BeforeEach
    void setUpAdmin() {
      BarberJwt adminJwt = new BarberJwt();
      adminJwt.setUserKind(BarberConstant.USER_KIND_ADMIN);
      when(userService.getAddInfoFromToken()).thenReturn(adminJwt);
    }

    @Test
    @DisplayName("Create province success")
    void testCreate_ValidProvince_ShouldSuccess(){
      CreateNationForm createNationForm = new CreateNationForm();
      createNationForm.setName("Hồ Chí Minh");
      createNationForm.setKind(BarberConstant.NATION_KIND_PROVINCE);

      when(nationRepository.existsByNameAndKind(createNationForm.getName(), createNationForm.getKind())).thenReturn(false);
      when(nationRepository.save(any())).thenAnswer(invocation -> {
        Nation nationMock = invocation.getArgument(0, Nation.class);
        nationMock.setId(999L);
        return nationMock;
      });

      ApiMessageDto<String> response = nationController.create(createNationForm, bindingResult);

      assertEquals(true, response.getResult());

      verify(nationRepository, times(1)).existsByNameAndKind(createNationForm.getName(), createNationForm.getKind());
      verify(nationMapper, times(1)).fromCreateNationFormToEntity(eq(createNationForm));
      verify(nationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Create province fails when name already exists")
    void testCreateNation_NameExists_ShouldFail() {
      CreateNationForm form = new CreateNationForm();
      form.setName("Bình Dương");
      form.setKind(BarberConstant.NATION_KIND_PROVINCE);

      when(nationRepository.existsByNameAndKind(form.getName(), form.getKind())).thenReturn(true);

      BadRequestException exception = assertThrows(BadRequestException.class, () -> {
        nationController.create(form, bindingResult);
      });

      assertEquals(ErrorCode.NATION_ERROR_EXIST, exception.getCode());

      verify(nationRepository, times(1)).existsByNameAndKind(form.getName(), form.getKind());
      verify(nationMapper, never()).fromCreateNationFormToEntity(any());
      verify(nationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create province fails when parentId != null")
    void testCreate_ProvinceWithParent_ShouldFail() {
      CreateNationForm createNationForm = new CreateNationForm();
      createNationForm.setName("Hà Nội");
      createNationForm.setKind(BarberConstant.NATION_KIND_PROVINCE);
      createNationForm.setParentId(1L);

      BadRequestException exception = assertThrows(BadRequestException.class, () -> {
        nationController.create(createNationForm, bindingResult);
      });

      assertEquals(ErrorCode.NATION_ERROR_NOT_PARENT, exception.getCode());

      verify(nationMapper, never()).fromCreateNationFormToEntity(any());
      verify(nationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create district fails when parentId = null")
    void testCreateNation_DistrictWithoutParent_ShouldFail() {
      CreateNationForm form = new CreateNationForm();
      form.setName("Quận 1");
      form.setKind(BarberConstant.NATION_KIND_DISTRICT);
      form.setParentId(null);

      BadRequestException exception = assertThrows(BadRequestException.class, () -> {
        nationController.create(form, bindingResult);
      });

      assertEquals(ErrorCode.NATION_ERROR_NOT_FOUND, exception.getCode());

      verify(nationMapper, never()).fromCreateNationFormToEntity(any());
      verify(nationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create district fails when parent parent is not a province")
    void testCreateNation_DistrictParentNotProvince_ShouldFail() {
      CreateNationForm form = new CreateNationForm();
      form.setName("Quận 1");
      form.setKind(BarberConstant.NATION_KIND_DISTRICT);
      form.setParentId(2L);

      when(nationRepository.findById(2L)).thenReturn(Optional.of(mockDistrict));

      BadRequestException exception = assertThrows(BadRequestException.class, () -> {
        nationController.create(form, bindingResult);
      });

      assertEquals(ErrorCode.NATION_ERROR_NOT_PARENT_PROVINCE, exception.getCode());

      verify(nationRepository, times(1)).findById(2L);
      verify(nationMapper, never()).fromCreateNationFormToEntity(any());
      verify(nationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get list nation success")
    void testGetListNationByAdmin_ValidCriteria_ShouldSuccess() {
      Pageable pageable = PageRequest.of(0, 10);
      List<Nation> nationList = Collections.singletonList(mockProvince);
      Page<Nation> nationPage = new PageImpl<>(nationList, pageable, 1);

      NationAdminDto dto = new NationAdminDto();
      dto.setId(1L);
      dto.setName("Bình Dương");
      dto.setKind(BarberConstant.NATION_KIND_PROVINCE);

      when(nationRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(nationPage);

      NationCriteria criteria = new NationCriteria();
      ApiMessageDto<ResponseListDto<List<NationAdminDto>>> response = nationController.listByAdmin(criteria, pageable);

      assertEquals("Bình Dương", response.getData().getContent().get(0).getName());
      assertEquals(1, response.getData().getTotalElements());
      assertEquals(1, response.getData().getTotalPages());
      assertEquals("Get list nation success", response.getMessage());

      verify(nationRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(nationMapper, times(1)).fromEntityToNationAdminDtoList(eq(nationList));
    }

    @Test
    @DisplayName("Get detail nation success")
    void testGetNationByAdmin_ExistingId_ShouldSuccess() {
      NationAdminDto mockDto = new NationAdminDto();
      mockDto.setId(1L);
      mockDto.setName("Bình Dương");
      mockDto.setKind(BarberConstant.NATION_KIND_PROVINCE);

      when(nationRepository.findById(1L)).thenReturn(Optional.of(mockProvince));

      ApiMessageDto<NationAdminDto> response = nationController.getByAdmin(1L);

      assertEquals(true, response.getResult());
      assertEquals(1L, response.getData().getId());
      assertEquals("Bình Dương", response.getData().getName());
      assertEquals(BarberConstant.NATION_KIND_PROVINCE, response.getData().getKind());

      verify(nationRepository, times(1)).findById(1L);
      verify(nationMapper, times(1)).fromEntityToNationAdminDto(eq(mockProvince));
    }

    @Test
    @DisplayName("Get detail nation not found")
    void testGetNationByAdmin_NotFound_ShouldFail() {
      when(nationRepository.findById(99L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> {
        nationController.getByAdmin(99L);
      });

      verify(nationRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Update district success")
    void testUpdateNation_ValidForm_ShouldSuccess() {
      UpdateNationForm updateForm = new UpdateNationForm();
      updateForm.setId(2L);
      updateForm.setName("Quận 1");
      updateForm.setKind(BarberConstant.NATION_KIND_DISTRICT);
      updateForm.setParentId(1L);

      Nation newDistrict = new Nation();
      newDistrict.setId(2L);
      newDistrict.setName("Quận 1");
      newDistrict.setKind(BarberConstant.NATION_KIND_DISTRICT);
      newDistrict.setParent(mockProvince);

      when(nationRepository.findById(2L)).thenReturn(Optional.of(mockDistrict));
      when(nationRepository.findById(1L)).thenReturn(Optional.of(mockProvince));
      when(nationRepository.existsByNameAndParentIdAndIdNot(anyString(), anyLong(), anyLong())).thenReturn(false);

      ApiMessageDto<String> response = nationController.update(updateForm, bindingResult);

      assertEquals(true, response.getResult());

      verify(nationRepository, times(1)).existsByNameAndParentIdAndIdNot(eq(updateForm.getName()), eq(updateForm.getParentId()), eq(updateForm.getId()));
      verify(nationRepository, times(1)).save(eq(mockDistrict));
      verify(nationMapper, times(1)).fromUpdateNationFormToEntity(eq(updateForm), eq(newDistrict));
    }

    @Test
    @DisplayName("Update province fails when name already exists")
    void testUpdateNation_NewNameExists_ShouldFail() {
      UpdateNationForm form = new UpdateNationForm();
      form.setId(1L);
      form.setName("Đồng Nai");
      form.setKind(BarberConstant.NATION_KIND_PROVINCE);

      Nation existingNation = new Nation();
      existingNation.setId(5L);
      existingNation.setName("Đồng Nai");
      form.setKind(BarberConstant.NATION_KIND_PROVINCE);

      when(nationRepository.findById(form.getId())).thenReturn(Optional.of(mockProvince));
      when(nationRepository.existsByNameAndKindAndIdNot(eq(form.getName()), eq(form.getKind()), eq(form.getId()))).thenReturn(true);

      BadRequestException exception = assertThrows(BadRequestException.class, () -> {
        nationController.update(form, bindingResult);
      });

      assertEquals("Nation name already exists at this kind", exception.getMessage());
      assertEquals(ErrorCode.NATION_ERROR_EXIST, exception.getCode());

      verify(nationRepository, times(1)).findById(form.getId());
      verify(nationRepository, times(1)).existsByNameAndKindAndIdNot(eq(form.getName()), eq(form.getKind()), eq(form.getId()));
      verify(nationMapper, never()).fromUpdateNationFormToEntity(any(), any());
      verify(nationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete province success")
    void testDeleteNation_Province_ShouldSuccess(){
      when(nationRepository.findById(mockProvince.getId())).thenReturn(Optional.of(mockProvince));

      ApiMessageDto<String> apiMessageDto = nationController.delete(mockProvince.getId());

      assertEquals(true, apiMessageDto.getResult());

      verify(branchRepository, times(1)).setNullProvinceByProvinceId(mockProvince.getId());
      verify(nationRepository, times(1)).setNullChildrenByParentId(mockProvince.getId());
      verify(nationRepository, times(1)).delete(mockProvince);
    }

    @Test
    @DisplayName("Delete district success")
    void testDeleteNation_District_ShouldSuccess() {
      when(nationRepository.findById(mockDistrict.getId())).thenReturn(Optional.of(mockDistrict));

      ApiMessageDto<String> apiMessageDto = nationController.delete(mockDistrict.getId());

      assertEquals(true, apiMessageDto.getResult());

      verify(branchRepository, times(1)).setNullDistrictByDistrictId(mockDistrict.getId());
      verify(nationRepository, times(1)).setNullChildrenByParentId(mockDistrict.getId());
      verify(nationRepository, times(1)).delete(mockDistrict);
    }

    @Test
    @DisplayName("Delete ward success")
    void testDeleteNation_Ward_ShouldSuccess() {
      when(nationRepository.findById(mockWard.getId())).thenReturn(Optional.of(mockWard));

      ApiMessageDto<String> apiMessageDto = nationController.delete(mockWard.getId());

      assertEquals(true, apiMessageDto.getResult());

      verify(branchRepository, times(1)).setNullWardByWardId(mockWard.getId());
      verify(nationRepository, never()).setNullChildrenByParentId(anyLong());
      verify(nationRepository, times(1)).delete(mockWard);
    }
  }

  @Nested
  @DisplayName("Public actions")
  class PublicTest{
    @Test
    @DisplayName("Get list nation success")
    void testGetListNationByClient_ValidCriteria_ShouldSuccess() {
      Pageable pageable = PageRequest.of(0, 10);
      List<Nation> nationList = Collections.singletonList(mockProvince);
      Page<Nation> nationPage = new PageImpl<>(nationList, pageable, 1);

      NationDto dto = new NationDto();
      dto.setId(1L);
      dto.setName("Bình Dương");
      dto.setKind(BarberConstant.NATION_KIND_PROVINCE);

      when(nationRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(nationPage);

      NationCriteria criteria = new NationCriteria();
      ApiMessageDto<ResponseListDto<List<NationDto>>> response = nationController.listByClient(criteria, pageable);

      assertEquals("Bình Dương", response.getData().getContent().get(0).getName());
      assertEquals(1, response.getData().getTotalElements());
      assertEquals(1, response.getData().getTotalPages());
      assertEquals("Get list nation success", response.getMessage());

      verify(nationRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(nationMapper, times(1)).fromEntityToNationDtoList(eq(nationList));
    }
  }
}
