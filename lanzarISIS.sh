#!/bin/bash


function verbose {
    echo "Práctica de multidifusión con ordenación total"
    echo "A continuación, especificar cada una de las ips en las que desplegar el
    servidor"

    maquinas=()
    puertos=()
    indice=1
    rm -f servidores.conf
    touch servidores.conf

    while true
    do
        read -p "Introducir la ip de la maquina > " ipMaquina
        read -p "Introducir el puerto por el que escuchar > " puertoMaquina

        if [ -z "$ipMaquina" ]
        then
            break;
        elif [ -z "$ipMaquina" ]
        then
            echo "Ip introducida no válida"
        elif [ -z "$puertoMaquina" ] || [ 0 -gt $puertoMaquina ] ||
            [ $puertoMaquina -gt 65535 ]
        then
            echo "Puerto introducido no válido"
        else
            echo "$((indice * 2 - 1));$ipMaquina;$puertoMaquina-" >> servidores.conf
            echo "$((indice * 2 ));$ipMaquina;$puertoMaquina-" >> servidores.conf
            maquinas[$indice]=$ipMaquina
            puertos[$indice]=$puertoMaquina
            indice=$((indice + 1))
        fi
    done


    echo "Desplegando en las maquinas"
    indice=1
    for ip in ${maquinas[@]};
    do
        echo "- Desplegando en $ip"
	echo "$((indice * 2 - 1));$((indice * 2))" > servidores.locales
        ssh $ip "rm -f -R ~/Documentos/trabajo_isis; mkdir ~/Documentos/trabajo_isis; JRE_HOME=; JAVA_HOME=; exit"
        scp tomcat.tar.gz servidores.conf servidores.locales i0917867@$ip:~/Documentos/trabajo_isis/
        ssh $ip "cd ~/Documentos/trabajo_isis; tar -zxf ~/Documentos/trabajo_isis/tomcat.tar.gz; rm ~/Documentos/trabajo_isis/tomcat.tar.gz;"
        echo "- - Arrancando servidor"
        #ssh $ip "cd trabajo_isis/tomcat/bin; chmod +x catalina.sh; bash startup.sh start; exit;"
	indice=$((indice + 1))
    done

    exit 0;
}

if [ -nz $1];
then
    case $1 in
        -h | --help)
            echo "Llamar sin argumentos o con la opción -v para el modo verboso"
            echo "Llamar con la opción -b  ip[,ip]... para lanzarlo en batch"
            echo "Usar la opción -p para especificar el puerto en modo batch.
            En caso de no especificarlo, se usará el 8080"
            ;;
        -v)
            verbose
            ;;
        *)
            verbose
            ;;
    esac
fi



