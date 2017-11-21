#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include "Clock.h"
#include <sys/syscall.h>
#include <sys/types.h>

void doWrites(char* file_name, int bufferSize, long numberOfWrites);
void doReads(char* file_name, int bufferSize, long numberOfReads);

int main(int argc, char* argv[]) {

	int buffer_size;
	int write_count;
	double time_diff;
	double start, end;
	char* file_name;

	if (argc == 7) {
	  
	  buffer_size = atoi(argv[2]);
	  file_name = argv[4];
	  write_count = atoi(argv[6]);
	  char* temp = argv[5];

	  if (temp[2] == 'w') {
	    
	    struct timespec value;
	    clock_gettime(CLOCK_BOOTTIME, &value);
	    start = getTotalMilliseconds(value);
	    doWrites(file_name, buffer_size, write_count);
	    //clock_gettime(CLOCK_BOOTTIME, &value);
	    //end = getTotalMilliseconds(value);

	    time_diff = (end - start);
	    
	    printf("Wrote a total of %d bytes\nProgram took %f milliseconds to finish\n", (write_count * buffer_size), start);
	    
	  } else if (temp[2] == 'r') {
	    
	    struct timespec value;
	    clock_gettime(CLOCK_BOOTTIME, &value);
	    start = getTotalMilliseconds(value);
	    doReads(file_name, buffer_size, write_count);
	    clock_gettime(CLOCK_BOOTTIME, &value);
	    end = getTotalMilliseconds(value);
	    
	    time_diff = end - start;

	    printf("Read a total of %d bytes\nProgram took %f seconds to finish\n", (write_count * buffer_size), start);
	    
	  }
	  
	} else if (argc == 1) {
	  int buffer_sizes[] = { 1024, 2048, 4096, 32768, 65536, 131072, 524288, 1048576 };
	  int write_counts[] = { 2097152, 1048576, 524288, 65536, 32768, 16384, 4096, 2048 };

	  int l = sizeof(buffer_sizes)/sizeof(buffer_sizes[0]);
	  
	  for (int i = 0; i < l; i++) {
	    char* file_name;
	    asprintf(&file_name, "test%i.txt", i);
	    struct timespec value;
	    clock_gettime(CLOCK_BOOTTIME, &value);
	    start = getTotalMilliseconds(value);
	    doWrites(file_name, buffer_sizes[i], write_counts[i]);
	    clock_gettime(CLOCK_BOOTTIME, &value);
	    end = getTotalMilliseconds(value);

	    time_diff = end - start;

	    double start1, end1;
	    clock_gettime(CLOCK_BOOTTIME, &value);
	    start1 = getTotalMilliseconds(value);
	    doReads(file_name, buffer_sizes[i], write_counts[i]);
	    clock_gettime(CLOCK_BOOTTIME, &value);
	    end1 = getTotalMilliseconds(value);

	    double time_diff1 = end1 - start1;

	    printf("%d,%d,%f,%f\n", buffer_sizes[i], write_counts[i],start,start1);

	  }
	} else {
	  printf("The incorrect number of arguments were passed.\nPlease pass values for file_name, buffer_size, and write_count.\n");
	}
	
	return 0;
}

void doWrites(char* file_name, int bufferSize, long numberOfWrites) {
  char buffer[bufferSize];

  int fileHandle = syscall(SYS_open, file_name, O_CREAT|O_TRUNC|O_RDWR);

  for (int i = 0; i < numberOfWrites; i++) {
    syscall(SYS_write, fileHandle, &buffer[0], bufferSize);
  }

  syscall(SYS_close, fileHandle);
}

void doReads(char* file_name, int bufferSize, long numberOfReads) {

  char buffer[bufferSize];

  int fileHandle = syscall(SYS_open, file_name);

  for(int i = 0; i < numberOfReads; i++) {
    syscall(SYS_read, fileHandle, &buffer[0], bufferSize);
  }

  syscall(SYS_close, fileHandle);
}
