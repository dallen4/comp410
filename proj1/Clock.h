#ifndef CLOCK
#define CLOCK

#include <time.h>

struct Time {
  struct timespec timeValue;
};

extern double getTotalMilliseconds();
extern struct Time getTime();

#endif
