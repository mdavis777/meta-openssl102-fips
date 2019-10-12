FIPSINC = ""
FIPSINC_class-target = "${@'' if d.getVar('OPENSSL_FIPS_ENABLED', True) != '1' else 'nss_fips.inc'}"

require ${FIPSINC}
