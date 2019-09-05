PACKAGECONFIG_append = " fips"

PACKAGECONFIG[fips] = "fips --with-fipsdir=${STAGING_DIR_TARGET}${libdir}/ssl/fips-2.0,,openssl-fips,,"

FILESEXTRAPATHS_prepend := "${THISDIR}/openssl:"

# This adds the necessary symbols if fips is enabled.
SRC_URI_append = " file://openssl-fips-version.patch \
                   file://0001-make-fips_premain_dso-support-cross-compiling.patch \
"

DEPENDS += "qemu-native"

inherit qemu

# We need to run the special fips_premain_dso under QEMU
do_compile_prepend() {
    qemu_binary="${@qemu_wrapper_cmdline(d, '${STAGING_DIR_HOST}', ['${STAGING_LIBDIR}','${STAGING_BASELIBDIR}'])}"
    cat << EOF > fips_premain_dso
#! /bin/sh
$qemu_binary fips_premain_dso.real "\$@"
EOF
    chmod +x fips_premain_dso
}
