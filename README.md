

# LEHSA - Lehigh Student Assistant

LEHSA is an android application that allows a student to register using their name, username and other details, lets them track tasks/ assignments, search faculty in their department as well as communicate with a chatbot to get information regarding their college.

## Features of LEHSA
  1. **User Registration and Login** : the student enters his/her first and last names, username, password and confirms password. On successful registration the user can login using the username and password.

  2. **Configuration** : The student can select the courses that he/she has registered to in that semester. Additionally, the user can either update the password or delete his/her account. This deleted account cannot be recovered.

  3. **Tracker** : The student can select the course name, enter the assignment title and set a date and time for the deadline of the assignment. LEHSA notifies the student once the deadline is approaching.

  4. **Interactive chatbot** : The student can either type or say the question regarding the university's infrastructure or any other student issues and the chatbot responds with either a solution or the link to a page with a possible solution.

## System Architecture

The system architecture of LEHSA consists of the following components:

  1. LEHSA Android Mobile Application

  2. MAMP Server

  3. PHPMyAdmin Database

  4. Wit AI chatbot

**LEHSA Application** : The core components of the LEHSA application are the Tracker, Configuration, Interactive Assistant, Faculty Search (implemented using the Tensor Flow model for image processing).

**MAMP Server** : The LEHSA application stores and retrieves information in the database via the PHP server.

**Database** : The PHPMyAdmin database is used to create the following tables in order to support LEHSA:

  1. assignment\_tracker : consists of information about the assignments/tasks that are added by the user in order to be tracked.

  2. chatbot : consists of the keywords received from witAI as well as the corresponding solutions or responses that must be provided to the user.

  3. faculty : contains information of each of the faculty like their contact information, office location etc. as well as their feature vectors.

  4. student\_course : contains information about the username of the students and the courses they have registered to

  5. student\_details : holds all the information entered while the student is registering to the app.

**Wit AI Chatbot** : The Interactive Assistant communicates with the Wit AI to detect key words from the users’ speech/typed input and uses it to retrieve the corresponding output from the database via the PHP server.

## Tools Required

### MAMP Server

  1. Install MAMP server for windows or Mac and follow the instructions on <https://documentation.mamp.info/en/MAMP-Mac/Installation/>

  2. Open and click on start to start the server.

  3. Click on webstart

  4. Under MySQL select PHPMyAdmin

  5. Either import your database .sql file or create a database.

  6. Move your script file to httdocs folder in the MAMP application

### Wit AI

  1. Login using your facebook id

  2. Create intent

  3. Add keywords and corresponding synonyms

## References

  1. Image recognition :
  <https://developers.google.com/ml-kit/vision/face-detection/android#java>

  2. Tracker :
  <https://data-flair.training/blogs/android-task-reminder-app/>

  3. Tensor Flow:
  <https://github.com/tensorflow>

  4. Android chatbot :
  <https://github.com/VidyasagarMSC/WatBot>
