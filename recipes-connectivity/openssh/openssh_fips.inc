FILESEXTRAPATHS_prepend := "${THISDIR}/openssh:"
DEPENDS += " \
    openssl-fips \
    fipscheck \
"
SRC_URI += " \
    file://0001-openssh-8.0p1-fips.patch \
"
