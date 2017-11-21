// Based off example by Sarah Kaylor (9/29/17)

#include "Clock.h"
#include <stdio.h>

double getTotalMilliseconds(struct timespec timeValue) {
  const double oneThousand = 1000;
  const double oneBillion = 1000000000;
  double milliseconds = 
    (timeValue.tv_sec) +
    (timeValue.tv_nsec / oneBillion);
  return milliseconds;
}

//struct Time getTime() {
//auto value = timespec();
//clock_gettime(CLOCK_BOOTTIME, &value);
//auto time = Time(value);
//return time;
//}


