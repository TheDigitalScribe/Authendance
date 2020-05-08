# Authendance Summary
For the Global Classroom module in TU Dublin. 2019/2020 academic year. DT282/3.

Authendance is a student attendance tracker app with QR code implementation, built using Java in Android Studio. Database implementation is accompished via Cloud Firestore. The app allows lecturers to generate QR codes, which students scan with their own Android phones to record their attendance for that specific module on the current date. Each QR code generated is unique. Lecturers can only generate QR codes for their own modules and students can only scan codes for modules they are enrolled in.

# Under The Hood
- Programming Language: Java
- IDE: Android Studio 3.6
- Minimum SDK version: API level 21 

# How it Works
Cloud Firestore handles the login authentication and reset password function. Each user in the database has a user_type field which determines if they are a student or teacher. When the user logs in, this field ensures they are directed to the correct dashboard page. If the incorrect email or password is entered, a Forgot Password prompt appears below which will direct the user to an activity that will allow them to reset their password.

The teacher can use a dropdown list to pick a module they want to generate a code for. Afterwards, the code is generated and is displayed on the screen. If the teacher already generated a code for that module on the current date, they will be informed the attendance will be overwritten if they continue. There is a button to allow the user to quit displaying the code, which deletes the QR code from the database. The screen is kept awake using a flag and if the user exits the activity using the back button, the QR code will be deleted. The QR code is made up of a randomised string of 26 numbers and letters. This string is stored in the qr_code field in Cloud Firestore under the corresponding module document.

The student chooses a module they want to scan a code for. If the user tries to pick a module that doesn't have a code currently generated, they will not be able to scan. If there is a code generated, the app requests permission from the user to use the camera. If it is granted, they will be able to scan codes. If they attempt to scan a code that isn't the same as the one stored in Firestore, they are informed it is invalid. Otherwise, their attendance is recorded for that module on the current date.

To show the list of modules for both teachers and students, a RecyclerView is implemented, along with an adapter, model class and Firestore boilerplate code. The modules are retrieved from the current user's database record.

ZXing library is used for scanning and generating QR codes. 
