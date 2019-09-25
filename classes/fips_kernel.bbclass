FILESEXTRAPATHS_prepend := "${LAYER_PATH_meta-openssl-one-zero-two-fips}/recipes-kernel/linux/files/:"
SRC_URI_append = " \
    file://crypto_fips.scc \
"
