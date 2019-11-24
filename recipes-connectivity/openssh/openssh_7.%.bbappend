FIPSINC = ""
FIPSINC_class-target = "${@'' if d.getVar('OPENSSL_FIPS_ENABLED', True) != '1' else 'openssh_fips.inc'}"

require ${FIPSINC}
