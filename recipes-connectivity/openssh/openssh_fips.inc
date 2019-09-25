FILESEXTRAPATHS_prepend := "${THISDIR}/openssh:"
DEPENDS += " \
    openssl-fips \
    fipscheck \
"
SRC_URI += " \
    file://0001-openssh-8.0p1-fips.patch \
"

do_install_append() {
    install -d ${D}${libdir}/fipscheck
}

inherit qemu

pkg_postinst_append_${PN}-ssh () {
    if [ -n "$D" ]; then
        if ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'true','false', d)}; then
            ${@qemu_run_binary(d, '$D', '${bindir}/fipshmac')} \
                -d $D${libdir}/fipscheck $D${bindir}/ssh.${BPN}
        else
            $INTERCEPT_DIR/postinst_intercept delay_to_first_boot ${PKG} mlprefix=${MLPREFIX}
        fi
    else
        ${bindir}/fipshmac -d ${libdir}/fipscheck ${bindir}/ssh.${BPN}
    fi
}

pkg_postinst_append_${PN}-sshd () {
    if [ -n "$D" ]; then
        if ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'true','false', d)}; then
            ${@qemu_run_binary(d, '$D', '${bindir}/fipshmac')} \
                -d $D${libdir}/fipscheck $D${sbindir}/sshd
        else
            $INTERCEPT_DIR/postinst_intercept delay_to_first_boot ${PKG} mlprefix=${MLPREFIX}
        fi
    else
        ${bindir}/fipshmac -d ${libdir}/fipscheck ${sbindir}/sshd
    fi
}

FILES_${PN} += "${libdir}/fipscheck"
