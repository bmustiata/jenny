
set -e

download_file() {
    url_to_download="$1"
    file_to_save="target/dependency/$2"

    if command -v curl; then
        curl "$url_to_download" --output "$file_to_save"
    else
        wget "$url_to_download" -O "$file_to_save"
    fi
}

mkdir -p target/dependency/

download_file https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/1.17/snakeyaml-1.17.jar snakeyaml-1.17.jar
download_file https://repo.maven.apache.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar commons-io-2.6.jar
download_file https://repo.maven.apache.org/maven2/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar commons-cli-1.3.1.jar
download_file https://repo.maven.apache.org/maven2/org/codehaus/groovy/groovy-all/2.4.15/groovy-all-2.4.15.jar groovy-all-2.4.15.jar

