SUMMARY = "A library for integrity verification of FIPS validated modules"
DESCRIPTION = "FIPSCheck is a library for integrity verification of FIPS validated \
modules. The package also provides helper binaries for creation and \
verification of the HMAC-SHA256 checksum files."
HOMEPAGE = "https://pagure.io/fipscheck"
SECTION = "libs/network"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=35f2904ce138ac5fa63e7cedf96bbedf"

SRC_URI = "https://releases.pagure.org/fipscheck/${BPN}-${PV}.tar.bz2 \
           file://0001-compat-fip-with-openssl-1.0.2.patch \
"
SRC_URI[md5sum] = "86e756a7d2aa15f3f91033fb3eced99b"
SRC_URI[sha256sum] = "7ba38100ced187f44b12dd52c8c74db8f366a2a8b9da819bd3e7c6ea17f469d5"

DEPENDS = " \
    openssl \
    openssl-fips \
"

inherit autotools pkgconfig

EXTRA_OECONF += " \
    --disable-static \
"
EXTRA_OEMAKE += " \
    -I${STAGING_LIBDIR_NATIVE}/ssl/fips-2.0/include \
"

