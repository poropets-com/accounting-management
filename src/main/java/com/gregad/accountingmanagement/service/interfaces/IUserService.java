package com.gregad.accountingmanagement.service.interfaces;


import com.gregad.accountingmanagement.dto.requestDto.EditProfileRequestDto;
import com.gregad.accountingmanagement.dto.responseDto.BlockUserResponseDto;
import com.gregad.accountingmanagement.dto.responseDto.RegisterUserResponseDto;
import com.gregad.accountingmanagement.dto.responseDto.UserInformationResponseDto;
import com.gregad.accountingmanagement.dto.requestDto.RegisterUserRequestDto;
import com.gregad.accountingmanagement.model.UserEntity;


public interface IUserService {
   UserEntity getUserEntityByEmail(String email);
   RegisterUserResponseDto addNewUser(RegisterUserRequestDto registerUserRequestDto);
   UserInformationResponseDto login(String email);
   UserInformationResponseDto getUser(String email);
   UserInformationResponseDto editUserData(String email,String token, EditProfileRequestDto editProfileRequestDto);
   UserInformationResponseDto removeUser(String email,String token);
   BlockUserResponseDto blockUser(String email, boolean blockStatus,String token);
}
