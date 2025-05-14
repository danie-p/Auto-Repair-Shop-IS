**An information system for recording vehicle service visits**, supporting the following operations (listed in order of expected frequency of use):
- searching for a vehicle by customer ID or license plate code and displaying all relevant vehicle details;
- adding a new vehicle;
- adding a new service visit for a vehicle (identified by customer ID or license plate code);
- updating any stored information, including vehicle identification data;
- deleting a service visit;
- deleting a vehicle.

The application is designed to store as much data as possible on **persistent storage** (e.g., hard drive).

The project uses **extendible hashing** without overflow files (i.e., direct hashing is applied).
An efficient management system for free blocks is implemented using linked block chains.
The project also makes use of a **heap file** stored on disk for appropriate operations.

For testing purposes, the application includes a GUI feature that allows users to display the full current contents of the database.
This includes the connections between blocks, any overflow or auxiliary files, and all relevant internal attributes.
The contents of all files can be displayed sequentially to visualize the internal structure.

This demo information system has been developed as a semester project for the _Algorithms and Data Structures 2_ course.
