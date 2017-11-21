CC=gcc
CFLAGS= -g -Wall -I.

all: experiment

experiment: main.o Clock.o
	$(CC) -o experiment main.o Clock.o

main.o: main.c Clock.h
	$(CC) $(CFLAGS) -c main.c -o main.o

Clock.o: Clock.c Clock.h
	$(CC) $(CLFAGS) -c Clock.c -o Clock.o

clean:
	rm -f *.o experiment
