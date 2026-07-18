package com.bulletinboard.BulletinBoard.build.impl.services;

import com.bulletinboard.BulletinBoard.build.api.dto.admin.AdResponseForAdminDto;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdCreateDTO;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdResponseForUserDto;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdUpdateDtoForUser;
import com.bulletinboard.BulletinBoard.build.db.entity.Ad;
import com.bulletinboard.BulletinBoard.build.db.enums.AdStatus;
import com.bulletinboard.BulletinBoard.build.db.repository.AdRepository;
import com.bulletinboard.BulletinBoard.build.impl.mappers.AdMapper;
import com.bulletinboard.BulletinBoard.common.exceptions.AdNotFoundException;
import com.bulletinboard.BulletinBoard.common.exceptions.UserNotFoundException;
import com.bulletinboard.BulletinBoard.user.db.entity.User;
import com.bulletinboard.BulletinBoard.user.db.enums.Role;
import com.bulletinboard.BulletinBoard.user.db.enums.UserStatus;
import com.bulletinboard.BulletinBoard.user.db.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;

    @Transactional(rollbackFor = Exception.class)
    public AdResponseForUserDto create(String loginUser, AdCreateDTO adCreateDTO) {
        User userSender = findByThrowUserNotFound(
                () -> userRepository.findByLogin(loginUser), loginUser
        );

        if (userSender.getRole().equals(Role.USER) && userSender.getStatus() == UserStatus.ACTIVE) {
            if (adCreateDTO.getPrice() < 0) {
                throw new IllegalArgumentException("Price cannot be less than zero");
            }
            Ad ad = adMapper.toEntity(adCreateDTO);
            ad.setStatus(AdStatus.ACTIVE);
            ad.setUser(userSender);
            ad.setCreatedAt(LocalDateTime.now());
            ad.setPrice(adCreateDTO.getPrice());
            ad.setCategory(adCreateDTO.getCategory());
            Ad savedAd = adRepository.save(ad);
            log.info("Ad created by user {}", loginUser);
            return adMapper.toResponseDtoForUser(savedAd);
        } else {
            throw new IllegalStateException("User is not active or not user");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public AdResponseForUserDto updateAdForUser(String login, Long adId, AdUpdateDtoForUser dto) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByLogin(login), login
        );

        Ad ad = findByThrowAdNotFound(
                () -> adRepository.findById(adId), adId.toString()
        );

        if (user.getRole().equals(Role.USER) && user.getStatus() == UserStatus.ACTIVE) {
            if (!ad.getUser().getId().equals(user.getId())) {
                log.warn("User {} tried to update ad {} that doesn't belong to them", login, adId);
                throw new AccessDeniedException("You can only update your own ads");
            }

            if (ad.getStatus() == AdStatus.BLOCKED || ad.getStatus() == AdStatus.DEACTIVATED) {
                log.warn("Cannot update ad with status: {}", ad.getStatus());
                throw new IllegalStateException("Cannot update ad with status: " + ad.getStatus());
            }

            if (dto.getName() != null && !dto.getName().isEmpty()) {
                ad.setName(dto.getName());
            }

            if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
                ad.setDescription(dto.getDescription());
            }

            if (dto.getCategory() != null && !dto.getCategory().isEmpty()) {
                ad.setCategory(dto.getCategory());
            }

            if (dto.getPrice() >= 0) {
                ad.setPrice(dto.getPrice());
            }

            Ad updatedAd = adRepository.save(ad);

            log.info("Ad {} updated by user {}", adId, login);
            return adMapper.toResponseDtoForUser(updatedAd);
        } else {
            throw new IllegalStateException("User is not active or not user");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteForAdmin(Long adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));
        adRepository.deleteById(ad.getId());
        log.info("Ad {} deleted by admin", adId);
    }

    @Transactional(rollbackFor = Exception.class)
    public AdResponseForUserDto deactivateAdForUser(String login, Long adId) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByLogin(login), login
        );

        Ad ad = findByThrowAdNotFound(
                () -> adRepository.findById(adId), adId.toString()
        );

        if (!ad.getUser().getId().equals(user.getId())) {
            log.warn("User {} tried to deactivate ad {} that doesn't belong to them", login, adId);
            throw new AccessDeniedException("You can only deactivate your own ads");
        }

        if (ad.getStatus() == AdStatus.BLOCKED) {
            log.warn("User {} tried to deactivate blocked ad {}", login, adId);
            throw new IllegalStateException("Cannot deactivate a blocked ad. Contact admin.");
        }

        if (ad.getStatus() == AdStatus.DEACTIVATED) {
            log.warn("User {} tried to deactivate already deactivated ad {}", login, adId);
            throw new IllegalStateException("Ad is already deactivated");
        }

        ad.setStatus(AdStatus.DEACTIVATED);
        Ad deactivatedAd = adRepository.save(ad);

        log.info("Ad {} deactivated by user {}", adId, login);
        return adMapper.toResponseDtoForUser(deactivatedAd);
    }

    @Transactional(rollbackFor = Exception.class)
    public AdResponseForUserDto activateAdForUser(String login, Long adId) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByLogin(login), login
        );

        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only activate your own ads");
        }

        if (ad.getStatus() == AdStatus.BLOCKED) {
            throw new IllegalStateException("Cannot activate a blocked ad. Contact admin.");
        }

        if (ad.getStatus() == AdStatus.ACTIVE) {
            throw new IllegalStateException("Ad is already active");
        }

        ad.setStatus(AdStatus.ACTIVE);
        Ad activatedAd = adRepository.save(ad);

        log.info("Ad {} activated by user {}", adId, login);
        return adMapper.toResponseDtoForUser(activatedAd);
    }

    public List<AdResponseForAdminDto> findAllForAdmin() {
        List<Ad> ads = adRepository.findAll();
        checkList(ads);
        return ads.stream()
                .map(adMapper::toResponseDtoForAdmin)
                .collect(Collectors.toList());
    }

    public List<AdResponseForUserDto> findAllForUser() {
        List<Ad> activeAds = adRepository.findAllForUser();
        checkList(activeAds);
        return activeAds.stream()
                .map(adMapper::toResponseDtoForUser)
                .collect(Collectors.toList());
    }

    public List<AdResponseForUserDto> findMyAdsForUser(String login) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByLogin(login), login
        );

        List<Ad> myAds = adRepository.findByUserId(user.getId());
        if (myAds.isEmpty()) {
            log.info("User {} has no ads", login);
            return Collections.emptyList();
        }

        return myAds.stream()
                .map(adMapper::toResponseDtoForUser)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public AdResponseForAdminDto blockAd(Long adId) {
        Ad ad = findByThrowAdNotFound(
                () -> adRepository.findById(adId), adId.toString()
        );

        if (ad.getStatus() == AdStatus.BLOCKED) {
            log.warn("Ad {} is already blocked", adId);
            throw new IllegalStateException("Ad is already blocked");
        }

        ad.setStatus(AdStatus.BLOCKED);
        Ad blockedAd = adRepository.save(ad);

        log.info("Ad {} blocked by admin", adId);
        return adMapper.toResponseDtoForAdmin(blockedAd);
    }

    @Transactional(rollbackFor = Exception.class)
    public AdResponseForAdminDto unblockAd(Long adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));

        if (ad.getStatus() != AdStatus.BLOCKED) {
            log.warn("Ad {} is not blocked", adId);
            throw new IllegalStateException("Ad is not blocked");
        }

        ad.setStatus(AdStatus.ACTIVE);
        Ad unblockedAd = adRepository.save(ad);

        log.info("Ad {} unblocked by admin", adId);
        return adMapper.toResponseDtoForAdmin(unblockedAd);
    }

    public List<AdResponseForUserDto> findByUserId(Long userId) {
        List<Ad> ads = adRepository.findByUserId(userId);
        checkList(ads);
        return ads.stream()
                .map(adMapper::toResponseDtoForUser)
                .collect(Collectors.toList());
    }

    public List<AdResponseForAdminDto> findByUserIdForAdmin(Long userId) {
        List<Ad> ads = adRepository.findByUserId(userId);
        checkList(ads);
        return ads.stream()
                .map(adMapper::toResponseDtoForAdmin)
                .collect(Collectors.toList());
    }

    public List<AdResponseForUserDto> findByUserLogin(String login) {
        List<Ad> ads = adRepository.findByUserLogin(login);
        checkList(ads);
        return ads.stream()
                .map(adMapper::toResponseDtoForUser)
                .collect(Collectors.toList());
    }

    public AdResponseForAdminDto findById(Long adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));
        return adMapper.toResponseDtoForAdmin(ad);
    }

    public AdResponseForUserDto findByIdForUser(Long adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found"));
        return adMapper.toResponseDtoForUser(ad);
    }

    public List<AdResponseForUserDto> findByName(String name) {
        List<Ad> ads = adRepository.findByName(name);
        if (ads.isEmpty()) {
            log.warn("No ads found with name: {}", name);
            return Collections.emptyList();
        }
        return ads.stream()
                .map(adMapper::toResponseDtoForUser)
                .collect(Collectors.toList());
    }

    public List<AdResponseForAdminDto> findByNameForAdmin(String name) {
        List<Ad> ads = adRepository.findByName(name);
        if (ads.isEmpty()) {
            return Collections.emptyList();
        }
        return ads.stream()
                .map(adMapper::toResponseDtoForAdmin)
                .collect(Collectors.toList());
    }

    public List<AdResponseForUserDto> findByNameAndUserLogin(String name, String userLogin) {
        List<Ad> ads = adRepository.findByNameAndUserLogin(name, userLogin);
        checkList(ads);
        return ads.stream()
                .map(adMapper::toResponseDtoForUser)
                .collect(Collectors.toList());
    }

    private void checkList(List<Ad> ads) {
        if (ads.isEmpty()) {
            log.warn("Ads not found");
            throw new AdNotFoundException("Ads not found");
        }
    }

    private Ad findByThrowAdNotFound(Supplier<Optional<Ad>> supplier, String value) {
        return supplier.get()
                .orElseThrow(() -> {
                    log.warn("Ad not found with id: {}", value);
                    return new AdNotFoundException("Ad with id " + value + " not found");
                });
    }

    private User findByThrowUserNotFound(Supplier<Optional<User>> supplier, String value) {
        return supplier.get()
                .orElseThrow(() -> {
                    log.warn("User not found with login: {}", value);
                    return new UserNotFoundException("User with login " + value + " not found");
                });
    }
}
