package com.example.toygry.one_you.common.constants;

/**
 * 사용자 역할 상수 클래스
 * 
 * 시스템에서 사용되는 모든 역할을 정의합니다.
 */
public final class Role {
    
    // 역할 값 (DB에 저장되는 값)
    public static final String STUDENT = "STUDENT";
    public static final String TEACHER = "TEACHER";
    public static final String ADMIN = "ADMIN";
    
    // Spring Security 권한 (ROLE_ 접두사 포함)
    public static final String ROLE_STUDENT = "ROLE_STUDENT";
    public static final String ROLE_TEACHER = "ROLE_TEACHER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    // 생성자를 private으로 설정하여 인스턴스화 방지
    private Role() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * 역할 값에 ROLE_ 접두사를 추가하여 Spring Security 권한으로 변환
     * 
     * @param role 역할 값 (예: "STUDENT")
     * @return Spring Security 권한 (예: "ROLE_STUDENT")
     */
    public static String toAuthority(String role) {
        return "ROLE_" + role;
    }
    
    /**
     * 주어진 역할이 유효한 역할인지 확인
     * 
     * @param role 확인할 역할
     * @return 유효한 역할이면 true, 그렇지 않으면 false
     */
    public static boolean isValidRole(String role) {
        return STUDENT.equals(role) || TEACHER.equals(role) || ADMIN.equals(role);
    }
}