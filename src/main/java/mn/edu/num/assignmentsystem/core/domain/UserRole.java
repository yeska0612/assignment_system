package mn.edu.num.assignmentsystem.core.domain;

/**
 * UserRole enum нь систем доторх хэрэглэгчийн эрхийн түвшинг илэрхийлнэ.
 *
 * Энэ project дээр role-based authorization бүрэн нэвтрээгүй ч
 * domain model requirement-г хангахын тулд role-г тусад нь тодорхойлж байна.
 */
public enum UserRole {
    ADMIN,
    STUDENT,
    TEACHER
}