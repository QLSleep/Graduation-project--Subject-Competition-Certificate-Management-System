# 竞赛证书系统 API 文档

> Base URL: `http://localhost:8888`
>
> 认证方式: JWT Bearer Token（除公开接口外，均需在 Header 中携带 `Authorization: Bearer <accessToken>`）
>
> 通用响应格式: `{ "code": 20000, "message": "操作成功", "data": { ... } }`

---

## 目录

- [通用说明](#通用说明)
- [认证相关 /auth](#认证相关-auth)
- [证书管理 /api/certificates](#证书管理-apicertificates)
- [用户管理 /api/admin/users](#用户管理-apiadminusers)
- [学院管理 /api/admin/college](#学院管理-apiadmincollege)
- [赛事管理 /api/events](#赛事管理-apievents)
- [赛届管理 /api/sessions](#赛届管理-apisessions)
- [科目组管理 /api/groups](#科目组管理-apigroups)
- [赛类管理 /api/categories](#赛类管理-apicategories)
- [公共接口](#公共接口)

---

## 通用说明

### 响应格式

```json
{
  "code": 20000,
  "message": "操作成功",
  "data": { ... }
}
```

| code | 说明 |
|------|------|
| 20000 | 成功 |
| 40000 | 参数错误 |
| 40100 | 未登录或 Token 已过期 |
| 40300 | 权限不足 |
| 40400 | 资源不存在 |
| 50000 | 业务异常 |
| 50001 | 系统异常 |

### 认证 Header

```
Authorization: Bearer <accessToken>
```

### 角色权限说明

| 角色 | 数据范围 | 可用操作权限 |
|------|----------|--------------|
| SYSTEM_ADMIN | ALL | CREATE, EDIT, DELETE, AUDIT, REISSUE, EXPORT, INVALID |
| COLLEGE_ADMIN | COLLEGE | CREATE, EDIT, AUDIT |
| REVIEWER | COLLEGE | AUDIT |
| TEACHER | RELATED | CREATE |
| STUDENT | SELF | CREATE |

### 证书状态

| 状态 | 说明 |
|------|------|
| PENDING_REVIEW | 待审核 |
| APPROVED | 已通过 |
| REJECTED | 已驳回（可重新提交） |
| REJECTED_NO_RESUBMIT | 已驳回（不可重新提交） |
| REISSUED | 已补发 |
| INVALID | 已作废 |

### 证书类型

| 类型 | 说明 |
|------|------|
| STUDENT | 学生证书 |
| INSTRUCTOR | 指导教师证书 |

### 审核动作

| 动作 | 说明 |
|------|------|
| APPROVE | 通过 |
| REJECT | 驳回（可重新提交） |
| REJECT_NO_RESUBMIT | 驳回（不可重新提交） |
| REISSUE | 补发 |
| INVALID | 作废 |

### 赛事级别

| 级别 | 说明 |
|------|------|
| SCHOOL | 校级 |
| CITY | 市级 |
| PROVINCIAL | 省级 |
| NATIONAL | 国家级 |
| GLOBAL | 全球级 |

---

## 认证相关 /auth

> 公开接口，无需认证（除 `/auth/me`、`/auth/update-email`、`/auth/send-update-email-code` 外）

### 1. 用户登录

```
POST /auth/login
```

**请求体:**

```json
{
  "username": "string",
  "password": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": {
    "accessToken": "string",
    "refreshToken": "string"
  }
}
```

---

### 2. 刷新 AccessToken

```
POST /auth/refresh
```

**请求体:**

```json
{
  "refreshToken": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": {
    "accessToken": "string"
  }
}
```

---

### 3. 登出

```
POST /auth/logout
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 4. 发送注册验证码

```
POST /auth/send-register-code
```

**请求体:**

```json
{
  "email": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 5. 发送找回密码验证码

```
POST /auth/send-reset-code
```

**请求体:**

```json
{
  "username": "string",
  "email": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 6. 重置密码

```
POST /auth/reset-password
```

**请求体:**

```json
{
  "username": "string",
  "email": "string",
  "code": "string",
  "newPassword": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 7. 用户注册

```
POST /auth/register
```

**请求体:**

```json
{
  "type": "STUDENT | TEACHER",
  "username": "string",
  "password": "string",
  "confirmPassword": "string",
  "realName": "string",
  "email": "string",
  "phone": "string",
  "collegeId": "number",
  "studentId": "string (type=STUDENT时)",
  "teacherId": "string (type=TEACHER时)",
  "verifyCode": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 8. 获取当前用户信息

```
GET /auth/me
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": {
    "id": "number",
    "username": "string",
    "realName": "string",
    "email": "string",
    "phone": "string",
    "studentId": "string",
    "teacherId": "string",
    "collegeId": "number",
    "collegeName": "string",
    "enabled": "boolean",
    "enabledText": "string",
    "roles": ["string"],
    "createdAt": "string"
  }
}
```

---

### 9. 发送修改邮箱验证码

```
POST /auth/send-update-email-code
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 10. 修改邮箱

```
POST /auth/update-email
```

**需要认证**

**请求体:**

```json
{
  "code": "string",
  "newEmail": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

## 证书管理 /api/certificates

### 1. 提交证书申请

```
POST /api/certificates
```

**需要认证 + CREATE 权限**

**请求体:**

```json
{
  "certType": "STUDENT | INSTRUCTOR",
  "certNumber": "string",
  "eventId": "number",
  "sessionId": "number",
  "groupId": "number",
  "categoryId": "number",
  "certTitle": "string",
  "awardLevel": "string",
  "issueDate": "string (yyyy-MM-dd)",
  "resourceKey": "string",
  "remark": "string",
  "students": [
    {
      "realName": "string",
      "studentId": "string"
    }
  ],
  "instructors": [
    {
      "realName": "string",
      "teacherId": "string"
    }
  ],
  "holder": {
    "realName": "string",
    "teacherId": "string"
  }
}
```

> `students` + `instructors`: STUDENT 类型证书使用
> `holder`: INSTRUCTOR 类型证书使用

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 2. 查询当前用户证书列表

```
GET /api/certificates/my
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": [
    {
      "id": "number",
      "certType": "STUDENT | INSTRUCTOR",
      "certNumber": "string",
      "collegeName": "string",
      "eventName": "string",
      "sessionName": "string",
      "groupName": "string",
      "categoryName": "string",
      "level": "string",
      "certTitle": "string",
      "awardLevel": "string",
      "issueDate": "string",
      "resourceKey": "string",
      "status": "CertStatus",
      "remark": "string",
      "rejectReason": "string",
      "recipients": [
        {
          "realName": "string",
          "studentId": "string"
        }
      ],
      "instructors": [
        {
          "realName": "string",
          "teacherId": "string"
        }
      ]
    }
  ]
}
```

---

### 3. 查询证书详情

```
GET /api/certificates/{certId}
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": {
    "id": "number",
    "certType": "STUDENT | INSTRUCTOR",
    "certNumber": "string",
    "collegeName": "string",
    "creatorName": "string",
    "submitterName": "string",
    "reviewerName": "string",
    "eventName": "string",
    "sessionName": "string",
    "groupName": "string",
    "categoryName": "string",
    "level": "string",
    "certTitle": "string",
    "awardLevel": "string",
    "issueDate": "string",
    "resourceKey": "string",
    "status": "CertStatus",
    "remark": "string",
    "rejectReason": "string",
    "reviewedAt": "string",
    "createdAt": "string",
    "updatedAt": "string",
    "recipients": [
      {
        "realName": "string",
        "studentId": "string"
      }
    ],
    "instructors": [
      {
        "realName": "string",
        "teacherId": "string"
      }
    ]
  }
}
```

---

### 4. 查询当前用户指导的学生证书列表

```
GET /api/certificates/my/students
```

**需要认证**

**响应:** 同接口 #2

---

### 5. 查询指定学院的所有证书

```
GET /api/certificates/college/{collegeId}
```

**需要认证**

**响应:** 同接口 #2

---

### 6. 查询所有证书（仅超管）

```
GET /api/certificates/all
```

**需要认证 + SYSTEM_ADMIN**

**响应:** 同接口 #2

---

### 7. 查询待审核证书列表

```
GET /api/certificates/pending
```

**需要认证 + AUDIT 权限**

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| collegeId | number | 否 | 学院ID（不传则根据权限范围返回） |

**响应:** 同接口 #2

---

### 8. 更新证书

```
PUT /api/certificates/{certId}
```

**需要认证 + EDIT 权限**

**请求体:** 同接口 #1

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 9. 删除证书（仅超管）

```
DELETE /api/certificates/{certId}
```

**需要认证 + DELETE 权限**

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 10. 上传证书图片

```
POST /api/certificates/{certId}/image
```

**需要认证**

**表单参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | JPG 图片，小于 3MB |

**响应:**

```json
{
  "code": 20000,
  "data": "string (resourceKey)"
}
```

---

### 11. 审核证书

```
POST /api/certificates/{certId}/review
```

**需要认证 + AUDIT 权限**

**请求体:**

```json
{
  "action": "APPROVE | REJECT | REJECT_NO_RESUBMIT | REISSUE | INVALID",
  "comment": "string (REJECT/REJECT_NO_RESUBMIT/INVALID 时必填)"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 12. 导出学院学生证书 Excel

```
GET /api/certificates/college/{collegeId}/export/student
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.xlsx`）

---

### 13. 导出学院指导教师证书 Excel

```
GET /api/certificates/college/{collegeId}/export/instructor
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.xlsx`）

---

### 14. 导出个人学生证书 Excel

```
GET /api/certificates/my/export/student
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.xlsx`）

---

### 15. 导出个人指导教师证书 Excel

```
GET /api/certificates/my/export/instructor
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.xlsx`）

---

### 16. 导出教师指导的学生证书 Excel

```
GET /api/certificates/my/students/export/student
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.xlsx`）

---

### 17. 导出全部学生证书 Excel

```
GET /api/certificates/all/export/student
```

**需要认证 + EXPORT 权限（仅超管）**

**响应:** 文件下载（`.xlsx`）

---

### 18. 导出全部指导教师证书 Excel

```
GET /api/certificates/all/export/instructor
```

**需要认证 + EXPORT 权限（仅超管）**

**响应:** 文件下载（`.xlsx`）

---

### 19. 下载证书图片

```
GET /api/certificates/{certId}/image/download
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.jpg`）

---

### 20. 导出指定学生的所有证书图片

```
GET /api/certificates/student/{studentId}/images/download
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.zip`）

---

### 21. 导出指定教师的所有证书图片

```
GET /api/certificates/teacher/{teacherId}/images/download
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.zip`）

---

### 22. 导出指定学院的所有证书图片

```
GET /api/certificates/college/{collegeId}/images/download
```

**需要认证 + EXPORT 权限**

**响应:** 文件下载（`.zip`，按证书编号子文件夹组织）

---

### 23. 导出所有学生的证书图片

```
GET /api/certificates/all/student/images/download
```

**需要认证 + EXPORT 权限（仅超管）**

**响应:** 文件下载（`.zip`，按学院子文件夹组织）

---

### 24. 导出所有教师的证书图片

```
GET /api/certificates/all/teacher/images/download
```

**需要认证 + EXPORT 权限（仅超管）**

**响应:** 文件下载（`.zip`，按学院子文件夹组织）

---

### 25. 统计当前教师 INSTRUCTOR 类型证书

```
GET /api/certificates/my/stats/instructor
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": {
    "total": "number",
    "approved": "number",
    "pendingReview": "number",
    "rejected": "number",
    "rejectedNoResubmit": "number",
    "invalid": "number"
  }
}
```

---

### 26. 统计当前教师指导过的学生数

```
GET /api/certificates/my/stats/students/count
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": "number"
}
```

---

### 27. 统计当前学生 STUDENT 类型证书

```
GET /api/certificates/my/stats/student
```

**需要认证**

**响应:** 同接口 #25

---

### 28. 统计学院证书（STUDENT类型）

```
GET /api/certificates/college/{collegeId}/stats/student
```

**需要认证**

**响应:** 同接口 #25

---

### 29. 统计学院证书（INSTRUCTOR类型）

```
GET /api/certificates/college/{collegeId}/stats/instructor
```

**需要认证**

**响应:** 同接口 #25

---

### 30. 统计所有证书（STUDENT类型，仅超管）

```
GET /api/certificates/all/stats/student
```

**需要认证 + SYSTEM_ADMIN**

**响应:** 同接口 #25

---

### 31. 统计所有证书（INSTRUCTOR类型，仅超管）

```
GET /api/certificates/all/stats/instructor
```

**需要认证 + SYSTEM_ADMIN**

**响应:** 同接口 #25

---

## 用户管理 /api/admin/users

### 1. 管理员创建用户

```
POST /api/admin/users
```

**需要认证 + CREATE 权限**

> 超管可创建所有类型用户，院管只能创建学生/教师

**请求体:**

```json
{
  "type": "STUDENT | TEACHER | COLLEGE_ADMIN | REVIEWER",
  "username": "string",
  "realName": "string",
  "email": "string",
  "phone": "string",
  "collegeId": "number",
  "studentId": "string (type=STUDENT时)",
  "teacherId": "string (type=TEACHER时)"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 2. 查看指定用户信息

```
GET /api/admin/users/{userId}
```

**需要认证 + EDIT 权限**

**响应:**

```json
{
  "code": 20000,
  "data": {
    "id": "number",
    "username": "string",
    "realName": "string",
    "email": "string",
    "phone": "string",
    "studentId": "string",
    "teacherId": "string",
    "collegeId": "number",
    "collegeName": "string",
    "enabled": "boolean",
    "enabledText": "string",
    "roles": ["string"],
    "createdAt": "string"
  }
}
```

---

### 3. 列表查看用户

```
GET /api/admin/users
```

**需要认证 + EDIT 权限**

> 超管：查询所有非超管用户；院管：查询本院师生

**响应:** 同接口 #2（数组）

---

### 4. 修改用户信息

```
PUT /api/admin/users/{userId}
```

**需要认证 + EDIT 权限**

> 超管：可修改所有字段；院管：可修改本院师生 username、realName、phone、studentId、teacherId；学生/教师：只能修改自己的 username、realName、phone

**请求体:**

```json
{
  "username": "string",
  "realName": "string",
  "phone": "string",
  "collegeId": "number",
  "studentId": "string",
  "teacherId": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 5. 启用/禁用用户

```
PATCH /api/admin/users/{userId}/enabled
```

**需要认证 + EDIT 权限**

> 超管：可禁用任意非超管用户；院管：可禁用本院师生（不能禁用自己或其他院管）

**请求体:**

```json
{
  "enabled": "boolean"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

## 学院管理 /api/admin/college

### 1. 新增学院

```
POST /api/admin/college
```

**需要认证 + CREATE 权限（仅超管）**

**请求体:**

```json
{
  "collegeName": "string",
  "collegeCode": "string",
  "description": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

### 2. 修改学院信息

```
PUT /api/admin/college/{collegeId}
```

**需要认证 + EDIT 权限（仅超管）**

**请求体:**

```json
{
  "collegeName": "string",
  "collegeCode": "string",
  "description": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": null
}
```

---

## 赛事管理 /api/events

### 1. 赛事列表

```
GET /api/events
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": [
    {
      "id": "number",
      "eventName": "string",
      "abbreviation": "string",
      "level": "SCHOOL | CITY | PROVINCIAL | NATIONAL | GLOBAL",
      "sponsor": "string",
      "organizer": "string",
      "description": "string"
    }
  ]
}
```

---

### 2. 创建赛事

```
POST /api/events
```

**需要认证**

**请求体:**

```json
{
  "eventName": "string",
  "abbreviation": "string",
  "level": "SCHOOL | CITY | PROVINCIAL | NATIONAL | GLOBAL",
  "sponsor": "string",
  "organizer": "string",
  "description": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": "number (eventId)"
}
```

---

## 赛届管理 /api/sessions

### 1. 赛届列表

```
GET /api/sessions
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": [
    {
      "id": "number",
      "sessionName": "string",
      "eventId": "number"
    }
  ]
}
```

---

### 2. 创建赛届

```
POST /api/sessions
```

**需要认证**

**请求体:**

```json
{
  "sessionName": "string",
  "eventId": "number"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": "number (sessionId)"
}
```

---

## 科目组管理 /api/groups

### 1. 科目组列表

```
GET /api/groups
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": [
    {
      "id": "number",
      "groupName": "string"
    }
  ]
}
```

---

### 2. 创建科目组

```
POST /api/groups
```

**需要认证**

**请求体:**

```json
{
  "groupName": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": "number (groupId)"
}
```

---

## 赛类管理 /api/categories

### 1. 赛类列表

```
GET /api/categories
```

**需要认证**

**响应:**

```json
{
  "code": 20000,
  "data": [
    {
      "id": "number",
      "categoryName": "string"
    }
  ]
}
```

---

### 2. 创建赛类

```
POST /api/categories
```

**需要认证**

**请求体:**

```json
{
  "categoryName": "string"
}
```

**响应:**

```json
{
  "code": 20000,
  "data": "number (categoryId)"
}
```

---

## 公共接口

> 无需认证

### 1. 系统欢迎页

```
GET /
```

**响应:** `"欢迎使用竞赛证书系统！"`

---

### 2. 系统信息

```
GET /api/info
```

**响应:** `"系统运行正常"`

---

### 3. 问候

```
GET /api/hello/{name}
```

**响应:** `"你好，{name}！欢迎使用竞赛证书系统。"`

---

## 附录：Excel 导出字段说明

`CertificateExportVO` 导出字段：

| 字段 | Excel列名 |
|------|-----------|
| seq | 序号 |
| collegeName | 学院 |
| eventName | 赛事名称 |
| level | 赛事级别 |
| awardLevel | 获奖等级 |
| studentsStr | 获奖学生 |
| instructorsStr | 指导教师 |
| sponsor | 主办单位 |
| remark | 备注 |

---

## 附录：Redis Key 说明

| Key Pattern | 说明 | 过期时间 |
|-------------|------|----------|
| `refresh:{userId}` | Refresh Token | 15天 |
| `email:code:{email}` | 邮箱验证码 | 5分钟 |
