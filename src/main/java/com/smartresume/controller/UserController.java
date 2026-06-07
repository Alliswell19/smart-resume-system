package com.smartresume.controller;

import com.smartresume.common.Result;
import com.smartresume.entity.User;
import com.smartresume.service.UserService;
import com.smartresume.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return Result.error(401, "未登录或token无效");
            }

            String jwtToken = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwtToken);

            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            return Result.success(user);
        } catch (Exception e) {
            return Result.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取个人资料
     */
    @GetMapping("/profile")
    public Result getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(401, "未登录或token无效");
            }

            User user = userService.getById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            // 构建个人资料数据
            Map<String, Object> profile = new HashMap<>();
            profile.put("userId", user.getId());
            profile.put("username", user.getUsername());
            profile.put("nickname", user.getNickname());
            profile.put("email", user.getEmail());
            profile.put("phone", user.getPhone());
            profile.put("avatar", user.getAvatar());
            profile.put("role", user.getRole());
            profile.put("status", user.getStatus());
            profile.put("createTime", user.getCreateTime());
            profile.put("lastLoginTime", user.getLastLoginTime());

            return Result.success(profile);
        } catch (Exception e) {
            return Result.error(500, "获取个人资料失败: " + e.getMessage());
        }
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    public Result updateUserProfile(@RequestHeader("Authorization") String token, 
                                   @RequestBody Map<String, Object> profileData) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(401, "未登录或token无效");
            }

            User user = userService.getById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            // 更新允许修改的字段
            if (profileData.containsKey("nickname")) {
                user.setNickname((String) profileData.get("nickname"));
            }
            if (profileData.containsKey("email")) {
                user.setEmail((String) profileData.get("email"));
            }
            if (profileData.containsKey("phone")) {
                user.setPhone((String) profileData.get("phone"));
            }

            boolean success = userService.updateById(user);
            if (success) {
                return Result.success("个人资料更新成功");
            } else {
                return Result.error(500, "个人资料更新失败");
            }
        } catch (Exception e) {
            return Result.error(500, "更新个人资料失败: " + e.getMessage());
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestHeader("Authorization") String token,
                              @RequestParam String avatarUrl) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(401, "未登录或token无效");
            }

            User user = userService.getById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            user.setAvatar(avatarUrl);
            boolean success = userService.updateById(user);
            
            if (success) {
                Map<String, String> resultData = new HashMap<>();
                resultData.put("avatarUrl", avatarUrl);
                return Result.success("头像上传成功", resultData);
            } else {
                return Result.error(500, "头像上传失败");
            }
        } catch (Exception e) {
            return Result.error(500, "头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/statistics")
    public Result getUserStatistics(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(401, "未登录或token无效");
            }

            // 返回简化的统计数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalResumes", 0);
            statistics.put("parsedResumes", 0);
            statistics.put("averageScore", 0);
            statistics.put("totalViews", 0);
            statistics.put("lastUploadTime", null);

            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(500, "获取用户统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result changePassword(@RequestHeader("Authorization") String token,
                                @RequestParam String oldPassword,
                                @RequestParam String newPassword) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(401, "未登录或token无效");
            }

            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error(400, "旧密码不正确");
            }
        } catch (Exception e) {
            return Result.error(500, "密码修改失败: " + e.getMessage());
        }
    }

    /**
     * 从token中获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return null;
            }

            String jwtToken = token.substring(7);
            return jwtUtil.getUserIdFromToken(jwtToken);
        } catch (Exception e) {
            return null;
        }
    }
}
