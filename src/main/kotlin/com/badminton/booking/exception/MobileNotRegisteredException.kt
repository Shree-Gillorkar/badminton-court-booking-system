package com.badminton.booking.exception

class MobileNotRegisteredException(mobileNumber: String) :
    RuntimeException("Mobile number $mobileNumber is not registered yet. Please register to continue.")