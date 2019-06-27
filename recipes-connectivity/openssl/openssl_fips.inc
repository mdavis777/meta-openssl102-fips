PACKAGECONFIG_append = " fips"

PACKAGECONFIG[fips] = "fips --with-fipsdir=${STAGING_DIR_TARGET}${libdir}/ssl/fips-2.0,,openssl-fips,,"

FILESEXTRAPATHS_prepend := "${THISDIR}/openssl:"

# This adds the necessary symbols if fips is enabled.
SRC_URI_append = " file://openssl-fips-version.patch"

inherit qemu

# We need to run the special fips_premain_dso under QEMU
do_compile_prepend() {
	oe_runmake depend
	oe_runmake fips_premain_dso

	mv fips_premain_dso fips_premain_dso.real
	cat << EOF > fips_premain_dso
#! /bin/sh

${@qemu_run_binary(d, '$D', 'fips_premain_dso.real')}
EOF
	chmod +x fips_premain_dso
}
