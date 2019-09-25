FIPSINC = ""
FIPSINC_class-target = "${@'' if d.getVar('OPENSSL_FIPS_ENABLED', True) != '1' else 'rng-tools_fips.inc'}"

require ${FIPSINC}
