package com.tarento.inventory.controller;

import com.tarento.inventory.controller.request.SearchFilter;
import com.tarento.inventory.controller.request.UserRequestDto;
import com.tarento.inventory.controller.response.*;
import com.tarento.inventory.entity.ElasticSearchUser;
import com.tarento.inventory.entity.MongoUser;
import com.tarento.inventory.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<ResponseDto> createUser(@RequestBody UserRequestDto userRequestDto){
        ElasticSearchUser user = userService.createUser(userRequestDto);
        return ResponseEntity.ok(makeUserResponseDto(user));
    }

    @GetMapping("/{id}")
    public MongoUser getUser(@PathVariable String id) {
        return userService.getUserById(id).orElse(null);
    }


    @GetMapping
    public ResponseEntity<ResponseDto> listUser(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        if (page == null || page < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("Page must be greater than zero"));
        }
        if (size == null || size < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("size must be greater than zero"));
        }
        Page<ElasticSearchUser> userPage = userService.listUser(page, size);
        ListUserResponseDto listUserResponseDto = new ListUserResponseDto(userPage.getTotalPages(),
                userPage.getTotalElements(), userPage.stream().map(this::makeUserResponseDto).toList());
        return ResponseEntity.ok(listUserResponseDto);
    }


    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDto> updateUser(@PathVariable("userId") String userId, @RequestBody  UserRequestDto userRequestDto) {
        ElasticSearchUser esUser = userService.updateUser(userId, userRequestDto);
        return ResponseEntity.ok(makeUserResponseDto(esUser));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    private UserResponseDto makeUserResponseDto(ElasticSearchUser user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .salary(user.getSalary())
                .isAdmin(user.getIsAdmin())
                .dob(user.getDob())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


    @PostMapping("/search")
    public List<ElasticSearchUser> searchUsers(@Valid @RequestBody SearchFilter filter) {
        return userService.searchUsers(filter);
    }
}

