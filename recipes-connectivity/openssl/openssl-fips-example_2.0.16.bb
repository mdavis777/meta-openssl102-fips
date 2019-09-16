SUMMARY = "OpenSSL FIPS Object Module v2.0 Example"
DESCRIPTION = "Integrate Appendix C Example OpenSSL Based Application \
in https://www.openssl.org/docs/fips/UserGuide-2.0.pdf"
HOMEPAGE = "http://www.openssl.org"
SECTION = "libs/network"

# "openssl | SSLeay" dual license
LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9a8f968107345e0b75aa8c2ecaa7ec8"

SRC_URI = "file://Makefile \
           file://fips_hmac.c \
           file://LICENSE \
"

S = "${WORKDIR}"

DEPENDS = " \
   openssl \
"

RDEPENDS_${PN} = " \
   openssl-fips-dev \
   openssl-dev \
   openssl-staticdev \
   packagegroup-core-buildessential \
"

FILES_${PN} += "${libdir}/ssl/fips-2.0/test"

do_configure[noexec] = "1"

do_compile() {
    # Cross compile case which linking to dynamic crypto library (libcrypto.so)
    ${CC} ${CFLAGS} -c ${WORKDIR}/fips_hmac.c -o ${WORKDIR}/fips_hmac.o
    ${CC} ${CFLAGS} -lcrypto ${LDFLAGS} ${WORKDIR}/fips_hmac.o -o ${WORKDIR}/fips_hmac.cross
}

do_install() {
    mkdir -p ${D}/${libdir}/ssl/fips-2.0/test
    install -m 755 ${WORKDIR}/fips_hmac.cross ${D}/${libdir}/ssl/fips-2.0/test

    # Native compile case which linking to static crypto library (libcrypto.a)
    mkdir -p ${D}/${libdir}/ssl/fips-2.0/test
    install -m 644 ${WORKDIR}/Makefile ${D}/${libdir}/ssl/fips-2.0/test
    install -m 644 ${WORKDIR}/fips_hmac.c ${D}/${libdir}/ssl/fips-2.0/test
    sed -i "s:@LIBDIR@:${libdir}:g" ${D}/${libdir}/ssl/fips-2.0/test/Makefile
}

INSANE_SKIP_${PN} += "dev-deps"

python __anonymous() {
    if d.getVar("OPENSSL_FIPS_ENABLED", True) != "1":
        raise bb.parse.SkipPackage("To enable the openssl-fips-example set OPENSSL_FIPS_ENABLED = '1'.")
}
