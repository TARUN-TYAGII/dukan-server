@echo off
echo ========================================
echo Testing School Book Shop APIs
echo ========================================

echo.
echo 1. Testing Books API...
curl -s http://localhost:8080/api/books
echo.

echo.
echo 2. Creating a Category...
curl -s -X POST http://localhost:8080/api/categories ^
-H "Content-Type: application/json" ^
-d "{\"name\": \"Grade 10 Books\", \"description\": \"Books for Grade 10 students\", \"categoryType\": \"GRADE_LEVEL\"}"
echo.

echo.
echo 3. Testing Categories API...
curl -s http://localhost:8080/api/categories
echo.

echo.
echo 4. Creating a Customer...
curl -s -X POST http://localhost:8080/api/customers ^
-H "Content-Type: application/json" ^
-d "{\"name\": \"Test Customer\", \"email\": \"test@example.com\", \"phone\": \"9876543210\", \"customerType\": \"INDIVIDUAL\"}"
echo.

echo.
echo 5. Testing Customers API...
curl -s http://localhost:8080/api/customers
echo.

echo.
echo ========================================
echo API Testing Complete!
echo ========================================
pause
