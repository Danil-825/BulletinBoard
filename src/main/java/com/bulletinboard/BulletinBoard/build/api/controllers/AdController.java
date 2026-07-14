package com.bulletinboard.BulletinBoard.build.api.controllers;

import com.bulletinboard.BulletinBoard.build.api.dto.admin.AdResponseForAdminDto;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdCreateDTO;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdResponseForUserDto;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdUpdateDtoForUser;
import com.bulletinboard.BulletinBoard.build.impl.services.AdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdController {
    private final AdService adService;



    @Operation(summary="Найти объявление по id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявление найдено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/admin/search_ad/id/{id}")
    public AdResponseForAdminDto findById(@PathVariable Long id) {
        return adService.findById(id);
    }

    @Operation(summary="обновить объявление")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "объявление обновлено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав на обновление"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @PatchMapping("/user/update_ad/{adId}")
    public ResponseEntity<AdResponseForUserDto> updateAdForUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("adId") Long adId,
            @RequestBody @Valid AdUpdateDtoForUser dto) {

        log.info("User {} updating ad {}", userDetails.getUsername(), adId);

        AdResponseForUserDto response = adService.updateAdForUser(
                userDetails.getUsername(), adId, dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary="Вывести активные объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявления найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/user/search/all_active")
    public List<AdResponseForUserDto> findAllForUser() {
        return adService.findAllForUser();
    }

    @Operation(summary="Вывести объявления юзера")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявления найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/user/search/my_ads")
    public List<AdResponseForUserDto> findMyAdsForUser(@AuthenticationPrincipal UserDetails login) {
        return adService.findMyAdsForUser(login.getUsername());
    }

    @Operation(summary="Вывести объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявления найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/admin/search_ads/all")
    public List<AdResponseForAdminDto> findAll() {
        return adService.findAllForAdmin();
    }


    @Operation(summary="Найти объявление по name")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявление найдено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/search/user_name/{name}")
    public List<AdResponseForUserDto> findByName(@PathVariable String name) {
        return adService.findByName(name);
    }


    @Operation(summary="Найти объявление по userId")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявление найдено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/search/user_id/{userId}")
    public List<AdResponseForUserDto> findByUserId(@PathVariable Long userId) {
        return adService.findByUserId(userId);
    }


    @Operation(summary="Найти объявления по email юзера")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявления найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/search/user_login/{login}")
    public List<AdResponseForUserDto> findByUserLogin(@PathVariable String login) {
        return adService.findByUserLogin(login);
    }

    @Operation(summary = "Создать объявление", description = "Регистрирует новое объявление")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявление создано"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @PostMapping("/user/create_ad")
    public AdResponseForUserDto create (@AuthenticationPrincipal UserDetails user, @Valid @RequestBody AdCreateDTO adCreateDTO) {
        return adService.create(user.getUsername(), adCreateDTO);
    }

    @Operation(summary="Удаление объявления", description = "Удаляет объявление")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "объявление удалено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @DeleteMapping("/admin/del_ad/{id}")
    public void delete (@PathVariable Long id) {
        adService.deleteForAdmin(id);
    }


    @Operation(summary="поиск всех объявлений юзера",
            description = "ищет объявления авторизованного юзера")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявления найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/user/search_is_active/all")
    public List<AdResponseForUserDto> findAllForUser (
            @AuthenticationPrincipal UserDetails currentUser) {
        return adService.findByUserLogin(currentUser.getUsername());
    }


    @Operation(summary="Ищет объявление пользователя по названию",
            description = "Ищет объявление по названию авторизованного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "объявление найдено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @GetMapping("/user/my_ads/{name}")
    public AdResponseForAdminDto findByNameAndUserLogin (@PathVariable String name,
                                                         @AuthenticationPrincipal UserDetails currentUser) {
        return adService.findByNameAndUserLogin(name, currentUser.getUsername());
    }

    @PatchMapping("/user/deactivate/{adId}")
    @Operation(summary = "Деактивировать своё объявление")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объявление деактивировано"),
            @ApiResponse(responseCode = "403", description = "Не ваше объявление"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "409", description = "Объявление уже деактивировано или заблокировано")
    })
    public ResponseEntity<AdResponseForUserDto> deactivateAd(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long adId) {
        return ResponseEntity.ok(adService.deactivateAdForUser(
                userDetails.getUsername(), adId
        ));
    }



    @PatchMapping("/user/activate/{adId}")
    @Operation(summary = "Активировать своё объявление")
    public ResponseEntity<AdResponseForUserDto> activateAd(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long adId) {
        return ResponseEntity.ok(adService.activateAdForUser(
                userDetails.getUsername(), adId
        ));
    }

    @PatchMapping("admin/block/{adId}")
    @Operation(summary = "Заблокировать объявление")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объявление заблокировано"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "409", description = "Объявление уже заблокировано")
    })
    public ResponseEntity<AdResponseForAdminDto> blockAd(@PathVariable Long adId) {
        return ResponseEntity.ok(adService.blockAd(adId));
    }

    @PatchMapping("admin/unblock/{adId}")
    @Operation(summary = "Разблокировать объявление")
    public ResponseEntity<AdResponseForAdminDto> unblockAd(@PathVariable Long adId) {
        return ResponseEntity.ok(adService.unblockAd(adId));
    }
}