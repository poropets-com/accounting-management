package com.gregad.accountingmanagement.service.impl;

import com.gregad.accountingmanagement.model.*;
import com.gregad.accountingmanagement.dto.requestDto.EditProfileRequestDto;
import com.gregad.accountingmanagement.dto.responseDto.BlockUserResponseDto;
import com.gregad.accountingmanagement.dto.responseDto.RegisterUserResponseDto;
import com.gregad.accountingmanagement.dto.responseDto.UserInformationResponseDto;
import com.gregad.accountingmanagement.repository.PostRepository;
import com.gregad.accountingmanagement.repository.UserTokenRepository;
import com.gregad.accountingmanagement.security.jwt.JwtTokenProvider;
import com.gregad.accountingmanagement.service.interfaces.IFavoritesService;
import com.gregad.accountingmanagement.service.interfaces.IRoleService;
import com.gregad.accountingmanagement.service.interfaces.IUserService;
import com.gregad.accountingmanagement.service.interfaces.IUserTokenService;
import lombok.extern.slf4j.Slf4j;
import com.gregad.accountingmanagement.dto.requestDto.RegisterUserRequestDto;
import com.gregad.accountingmanagement.repository.RoleRepository;
import com.gregad.accountingmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountingService implements IUserService, IRoleService, IFavoritesService, IUserTokenService {
    private static final String DEFAULT_AVATAR = "/default_avatar.png";
    private static UserRepository userRepository;
    private static RoleRepository roleRepository;
    private static PostRepository postRepository;
    private static UserTokenRepository userTokenRepository;
    private static BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    

    @Autowired
    public AccountingService(UserRepository userRepository, 
                             RoleRepository roleRepository, 
                             PostRepository postRepository,
                             UserTokenRepository userTokenRepository,
                             BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.postRepository=postRepository;
        this.userTokenRepository=userTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserEntity getUserEntityByEmail(String email){
        return userRepository.findById(email).orElse(null);
    }
//////////////////////////////////////////////////////////////////////////////////////////
    @Override
    @Transactional
    public RegisterUserResponseDto addNewUser(RegisterUserRequestDto registerUserRequestDto) {
        if (registerUserRequestDto.getEmail()==null || 
        registerUserRequestDto.getName()==null || 
        registerUserRequestDto.getPassword()==null){
            throwBadRequestException("IN registration user email, name, password are required fields");
        }
        if(userRepository.existsById(registerUserRequestDto.getEmail())){
            log.error("IN register-email {} already exists",registerUserRequestDto.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT,"email already in use");
        }
        RoleEntity roleEntity=roleRepository.findById("ROLE_"+Role.USER).orElse(null);
        UserEntity userEntity=new UserEntity();
        userEntity.setEmail(registerUserRequestDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(registerUserRequestDto.getPassword()));
        userEntity.setName(registerUserRequestDto.getName());
        userEntity.setPhone("");
        userEntity.setStatus(Status.ACTIVE);
        userEntity.setAvatar(DEFAULT_AVATAR);
        userEntity.setRoles(new ArrayList<RoleEntity>(Arrays.asList(roleEntity)));
        userEntity.setFavorites(new ArrayList<PostEntity>());
        userRepository.save(userEntity);
        log.info("IN register - user {} successfully registered",registerUserRequestDto.getEmail());
        return new RegisterUserResponseDto(DEFAULT_AVATAR,
                userEntity.getName(),
                userEntity.getEmail(),
                toRolesHashSet(userEntity.getRoles()));
    }
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserInformationResponseDto login(String email) {
        UserEntity userEntity=userRepository.findById(email).orElse(null);
        log.info("IN login - user {} successfully identified",email);
        return toUserInformationResponseDto(userEntity);
    }


    private HashSet<String> toRolesHashSet(List<RoleEntity> roles) {
        return roles.stream().map(r->r.getName()).collect(Collectors.toCollection(HashSet::new));
    }
//////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserInformationResponseDto getUser(String email) {
        UserEntity userEntity= userRepository.findById(email).orElse(null);
        if (userEntity==null) {
            throwBadRequestException("IN userInformation request used wrong email " + email);
        }
        return toUserInformationResponseDto(userEntity);
    }

    private UserInformationResponseDto toUserInformationResponseDto(UserEntity userEntity) {
    return new UserInformationResponseDto(userEntity.getAvatar(),
            userEntity.getName(),
            userEntity.getEmail(),
            userEntity.getPhone(),
            !userEntity.getStatus().equals(Status.ACTIVE),
            toRolesHashSet(userEntity.getRoles()));
    }
//////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserInformationResponseDto editUserData(String email,String token, EditProfileRequestDto editProfileRequestDto) {
        String emailFromToken=getEmailFromToken(token);
        if (!email.equals(emailFromToken)){
            throwBadRequestException("IN editUserData- user with email "+email+" added wrong email "+emailFromToken+" to url");
        }
        UserEntity userEntity= userRepository.findById(email).orElse(null);
        if (userEntity==null){
            throwBadRequestException("IN editUserData used wrong email "+email);
        }
        userEntity.setAvatar(editProfileRequestDto.getAvatar());
        userEntity.setName(editProfileRequestDto.getName());
        userEntity.setPhone(editProfileRequestDto.getPhone());
        
        userRepository.save(userEntity);
        log.info("IN editUserData user- {} successfully edited",email);
        return toUserInformationResponseDto(userEntity);
    }
//////////////////////////////////////////////////////////////////////////////////////////
    @Override
    @Transactional
    public UserInformationResponseDto removeUser(String email,String token) {
        String emailFromToken=getEmailFromToken(token);
        if (!email.equals(emailFromToken)){
            throwBadRequestException("IN delete- user with email "+email+
                    " to url added wrong email "+emailFromToken);
        }
        UserEntity userEntity=userRepository.findById(email).orElse(null);
        if (userEntity==null){
           throwBadRequestException("IN removeUser used wrong email "+email);
        }
        userRepository.deleteById(email);
        log.info("IN removeUser- user with email {} successfully removed",email);
        return toUserInformationResponseDto(userEntity);//TODO
    }
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    @Transactional
    public Set<String> addRole(String email, String requestRole) {
        String role="ROLE_"+requestRole;
        UserEntity userEntity= getUserEntityByEmail(email);
        if (userEntity==null){
            throwBadRequestException("IN addRole used wrong email "+email);
        }
        RoleEntity roleEntity=roleRepository.findById(role).orElse(null);
        if (roleEntity==null){
            List<String>roles=roleRepository.findAll().stream().map(r->r.getName()).collect(Collectors.toList());
            log.error("IN addRole used invalid role name {}",role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"used invalid role name "+role+
                    " here is a list of valid role names"+roles);
        }
        userEntity.getRoles().add(roleEntity);
        userRepository.save(userEntity);
        log.info("IN addRole to user {} successfully added role {}",email,role);
        return toRolesHashSet(userEntity.getRoles());
    }
///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    @Transactional
    public Set<String> removeRole(String email, String requestRole) {
        String role="ROLE_"+requestRole;
        UserEntity userEntity= getUserEntityByEmail(email);
        if (userEntity==null){
            throwBadRequestException("IN removeRole used wrong email "+email);
        }
        RoleEntity roleEntity=roleRepository.findById(role).orElse(null);
        if (roleEntity==null){
            List<String>roles=roleRepository.findAll().stream().map(r->r.getName()).collect(Collectors.toList());
            log.error("IN removeRole used invalid role name {}",role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"used invalid role name "+role+
                    " here is a list of valid role names"+roles);
        }
        userEntity.getRoles().remove(roleEntity);
        userRepository.save(userEntity);
        log.info("IN removeRole to user {} successfully added role {}",email,role);
        return toRolesHashSet(userEntity.getRoles());
    }
///////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public BlockUserResponseDto blockUser(String email, boolean blockStatus,String token) {
        String emailFromToken=getEmailFromToken(token);
        if (email.equals(emailFromToken)){
            throwBadRequestException("IN block used wrong email\"+\n" +
                            email+" Attention! you trying to block yourself");
        }
        UserEntity userEntity= getUserEntityByEmail(email);
        if (userEntity==null){
            log.error("IN block used wrong email {}",email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"used wrong email");
        }
        if (blockStatus){
            userEntity.setStatus(Status.NOT_ACTIVE);
            log.info("IN blockUser user- {} successfully blocked",email);
        }else {
            userEntity.setStatus(Status.ACTIVE);
            log.info("IN blockUser user- {} successfully unblocked",email);
        }
        userRepository.save(userEntity);
        return new BlockUserResponseDto(email,userEntity.getStatus().equals(Status.NOT_ACTIVE));
    }

    private void throwBadRequestException(String message) {
        log.error(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,message);
    }
///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void addToken(String email,String token) {
        UserTokenEntity userTokenEntity=new UserTokenEntity(email,token);
        userTokenRepository.save(userTokenEntity);
    }

    @Override
    public void deleteToken(String email) {
        if (userTokenRepository.existsById(email)) {
            userTokenRepository.deleteById(email);
        }
    }

    @Override
    public boolean tokenValidation(String email,String jwtToken) {
        UserTokenEntity userTokenEntity=userTokenRepository.findById(email).orElse(null);
        return userTokenEntity.getToken().equals(jwtToken);
    }
//////////////////////////////////////////////////////////////////////////////////////
    @Override
    @Transactional
    public List<String> addFavorite(String email, String postId, String token) {
        String emailFromToken=getEmailFromToken(token);
        if (!email.equals(emailFromToken)){
            throwBadRequestException("IN addFavorites- user with email "+email+" added wrong email "+emailFromToken+" to url");
        }
        UserEntity userEntity=userRepository.findById(email).orElse(null);
        if (userEntity==null){
            throwBadRequestException("IN addFavorites- wrong email"+email);
        }
        PostEntity postEntity=postRepository.findById(postId).orElse(null);
        if (postEntity==null){
            postEntity=new PostEntity(postId);
        }
        postRepository.save(postEntity);
        if (!userEntity.getFavorites().contains(postEntity)) {
            userEntity.getFavorites().add(postEntity);
        }
        userRepository.save(userEntity);
        log.info("IN addFavorites- post {} successfully added to user {}",postId,email);
        return userEntity.getFavorites().stream().map(p->p.getPostId()).collect(Collectors.toList());
    }
    

    @Override
    @Transactional
    public List<String> removeFavorite(String email, String postId, String token) {
        String emailFromToken=getEmailFromToken(token);
        if (!email.equals(emailFromToken)){
            throwBadRequestException("IN removeFavorites- user with email "+email+" added wrong email "+emailFromToken+" to url");
        }       
        UserEntity userEntity=userRepository.findById(email).orElse(null);
        if (userEntity==null){
            throwBadRequestException("IN removeFavorites- wrong email"+email);
        }
        PostEntity postEntity=postRepository.findById(postId).orElse(null);
        
        postRepository.deleteById(postId);
        userEntity.getFavorites().remove(postEntity);
        userRepository.save(userEntity);
        log.info("IN removeFavorites- post {} successfully added to user {}",postId,email);
        return userEntity.getFavorites().stream().map(p->p.getPostId()).collect(Collectors.toList());
    }

    @Override
    public List<String> getFavorites(String email) {
        UserEntity userEntity=userRepository.findById(email).orElse(null);
        if (userEntity==null){
            throwBadRequestException("IN getFavorites- wrong email "+email);
        }
        
        return userEntity.getFavorites().stream().map(p->p.getPostId()).collect(Collectors.toList());
    }

    private String getEmailFromToken(String token) {
        String resolvedToken=token.substring(7,token.length());
        return jwtTokenProvider.getUserEmail(resolvedToken);
    }
}
