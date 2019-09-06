SUMMARY = "OpenSSL FIPS Object Module v2.0"
DESCRIPTION = "OpenSSL FIPS Object Module v2.0"
HOMEPAGE = "http://www.openssl.org"
BUGTRACKER = "http://www.openssl.org/news/vulnerabilities.html"
SECTION = "libs/network"

# "openssl | SSLeay" dual license
LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9a8f968107345e0b75aa8c2ecaa7ec8"

# Set "OPENSSL_FIPS_PREBUILT" to the location of the prebuilt
# openssl-fips-TARGET_ARCH-install.tar.bz2 files.
#
FILESEXTRAPATHS_prepend := "${OPENSSL_FIPS_PREBUILT}:"

PREBUILT_OPENSSL_FIPS = "openssl-fips-${PV}-${TARGET_ARCH}-install.tar.bz2"

SRC_URI = "file://${PREBUILT_OPENSSL_FIPS} \
           file://LICENSE \
"
S = "${WORKDIR}"

RDEPENDS_${PN}-dev = ""
FILES_${PN}-dev += "${bindir}/fipsld ${libdir}/ssl/fips-2.0"

INHIBIT_PACKAGE_DEBUG_SPLIT = '1'
INHIBIT_PACKAGE_STRIP = '1'

do_configure[noexec] = "1"
do_compile[noexec] = "1"

LIBRELBIN = "${@oe.path.relative(d.getVar('bindir', True), d.getVar('libdir', True))}"

do_install() {
	mkdir -p ${D}/${prefix}
	mkdir -p ${D}/${bindir}
	mkdir -p ${D}/${libdir}
	cp -dR --preserve=mode,timestamps ${WORKDIR}/usr/local/ssl ${D}/${libdir}/.

	# Create a small wrapper so fipsld works as expected...
	cat << EOF > ${D}/${bindir}/fipsld
#!/bin/sh
runpath=\`dirname \$0\`
if [ "\$runpath" = "." -a ! -x "./\$0" ]; then
   runpath=\`which \$0\`
   runpath=\`dirname \$runpath\`
fi
runpath=\`cd \$runpath && pwd\`
exec \$runpath/${LIBRELBIN}/ssl/fips-2.0/bin/fipsld "\$@"
EOF
	chmod 0755 ${D}/${bindir}/fipsld

	# We want the fipsld script to fall back to openssl for sha1
	# processing
	rm ${D}/${libdir}/ssl/fips-2.0/bin/fips_standalone_sha1
        cat << EOF > ${D}/${libdir}/ssl/fips-2.0/bin/fips_standalone_sha1
#!/bin/sh
HMAC_KEY="etaonrishdlcupfm"
openssl sha1 -hmac \${HMAC_KEY} "\$@"
EOF
	chmod 0755 ${D}/${libdir}/ssl/fips-2.0/bin/fips_standalone_sha1
	sed -e 's,FINGERTYPE="$''{THERE}/bin/openssl sha1 -hmac $''{HMAC_KEY}",FINGERTYPE="openssl sha1 -hmac $''{HMAC_KEY}",' \
		-i ${D}/${libdir}/ssl/fips-2.0/bin/fipsld
}

# The following is used to ensure that if the user changes the archive
# we rebuild properly.
#
# Note: this does not cover the case where the archive was not present
# and then has been added.  In this case the user must manually clear
# the tmp-glibc/cache directory (or otherwise force the cache
# to be rebuilt.)
#
OPENSSL_FIPS_FULLPATH = "${@which(d.getVar('FILESPATH', True), d.getVar('PREBUILT_OPENSSL_FIPS', True)) or ""}"
OPENSSL_FIPS_MTIME = "${@os.path.getmtime(d.getVar('OPENSSL_FIPS_FULLPATH', True)) if os.path.exists(d.getVar('OPENSSL_FIPS_FULLPATH', True)) else ""}"
do_unpack[vardeps] += "OPENSSL_FIPS_FULLPATH OPENSSL_FIPS_MTIME"

def which(paths, file):
    for path in paths.split(':'):
        fullpath = os.path.join(path, file)
        if os.path.exists(fullpath):
            return fullpath
    return

OPENSSL_FIPS_PREBUILT ?= "${FILE_DIRNAME}/${BPN}"

addtask do_check_fips before do_fetch
python do_check_fips() {
    if d.getVar('OPENSSL_FIPS_FULLPATH', True) == "":
        msg = "Support for %s (%s) is not available in %s." % \
               (d.getVar('TARGET_ARCH', True) or "", \
                d.getVar('PREBUILT_OPENSSL_FIPS', True),
                d.getVar('OPENSSL_FIPS_PREBUILT', True)) + \
                   " See %s/README.build for more information." % (d.getVar('LAYERPATH_meta-openssl-one-zero-two-fips', True) or "")
        raise bb.fatal(msg)
}

python __anonymous() {
    if d.getVar("OPENSSL_FIPS_ENABLED", True) != "1":
        raise bb.parse.SkipPackage("To enable the openssl-fips recipe use the feature/openssl-fips template.")
}

# Workaround warning `Unable to get checksum for lib32-openssl-fips
# SRC_URI entry openssl-fips-2.0.16-i686-install.tar.bz2: file could
# not be found'
do_fetch[file-checksums] = " ${@get_lic_checksum_file_list(d)}"
