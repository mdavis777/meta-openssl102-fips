CC = gcc
OPENSSLDIR = @LIBDIR@/ssl/fips-2.0/
LIBCRYPTO = @LIBDIR@/libcrypto.a
INCLUDES =

CMD = fips_hmac
OBJS = $(CMD).o
$(CMD): $(OBJS)
	FIPSLD_CC=$(CC) $(OPENSSLDIR)/bin/fipsld -o $(CMD) $(OBJS) $(LIBCRYPTO) -ldl
$(OBJS): $(CMD).c
	$(CC) -c $(CMD).c $(INCLUDES)
clean:
	rm $(OBJS)
