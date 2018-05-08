#!/bin/bash


function verbose {
    echo "Práctica de multidifusión con ordenación total"
    echo "A continuación, especificar cada una de las ips en las que desplegar el
    servidor"

    maquinas=()
    indice=1
    rm -f servidores.conf
    touch servidores.conf
    rm -f ips.txt
    touch ips.txt

    while true
    do
        read -p "Introducir la ip de la maquina > " ipMaquina

        if [ -z "$ipMaquina" ]
        then
            break;
        elif [ -z "$ipMaquina" ]
        then
            echo "Ip introducida no válida"
        else
	    echo "$ipMaquina" >> ips.txt
            echo "$((indice * 2 - 1));$ipMaquina;" >> servidores.conf
            echo "$((indice * 2 ));$ipMaquina;" >> servidores.conf
            maquinas[$indice]=$ipMaquina
            indice=$((indice + 1))
        fi
    done


    echo "Desplegando en las maquinas"
    indice=1
    for ip in ${maquinas[@]};
    do
        echo "- Desplegando en $ip"
	echo "$((indice * 2 - 1));$((indice * 2))" > servidores.locales
        ssh i0917867@$ip "rm -f -R ~/Documentos/trabajo_isis; 
			  mkdir ~/Documentos/trabajo_isis; 
			  JRE_HOME=/opt/jdk1.8.0_60; 
			  JAVA_HOME=/opt/jdk1.8.0_60; 
			  CATALINA_HOME=~/Documentos/trabajo_isis/TomcatServer/bin"
        scp tomcat.tar.gz servidores.conf servidores.locales i0917867@$ip:~/Documentos/trabajo_isis/
        ssh -t i0917867@$ip "cd ~/Documentos/trabajo_isis; 
			  tar -zxf ~/Documentos/trabajo_isis/tomcat.tar.gz; 
			  rm ~/Documentos/trabajo_isis/tomcat.tar.gz;
		          echo '- - Arrancando servidor';
		          cd TomcatServer/bin; 
 		          bash catalina.sh start"
	indice=$((indice + 1))
    done

    exit 0;
}

function notverbose {
    maquinas=()
    indice=1
    rm -f servidores.conf
    touch servidores.conf
    rm -f ips.txt
    touch ips.txt

    for ipMaquina in $@
    do
	echo "$ipMaquina" >> ips.txt
        echo "$((indice * 2 - 1));$ipMaquina;" >> servidores.conf
	echo "$((indice * 2 ));$ipMaquina;" >> servidores.conf
	maquinas[$indice]=$ipMaquina
	indice=$((indice + 1))
    done


    echo "Desplegando en las maquinas"
    indice=1
    for ip in ${maquinas[@]};
    do
        echo "- Desplegando en $ip"
	echo "$((indice * 2 - 1));$((indice * 2))" > servidores.locales
        ssh i0917867@$ip "rm -f -R ~/Documentos/trabajo_isis; 
			  mkdir ~/Documentos/trabajo_isis;"
        scp tomcat.tar.gz servidores.conf servidores.locales i0917867@$ip:~/Documentos/trabajo_isis/
        ssh i0917867@$ip "export JRE_HOME=/opt/jdk1.8.0_60; 
			  export JAVA_HOME=/opt/jdk1.8.0_60; 
			  export CATALINA_HOME=~/Documentos/trabajo_isis/TomcatServer/
		          cd ~/Documentos/trabajo_isis; 
			  tar -zxf ~/Documentos/trabajo_isis/tomcat.tar.gz; 
			  rm ~/Documentos/trabajo_isis/tomcat.tar.gz;
		          echo '- - Arrancando servidor';
		          cd TomcatServer/bin; 
			  bash catalina.sh stop;
 		          bash catalina.sh start"
	indice=$((indice + 1))
    done

    exit 0;
}

if [ $# -gt 0 ]
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
	-start)
	    curl -X PUT "http://localhost:8080/SD_ISIS/dispatcher/start"	
	    ;;
	-logs)
	    curl -X GET "http://localhost:8080/SD_ISIS/dispatcher/comprobarResultados"	
	    ;;
	-rm)
	    maquinas=()
	    indice=1
	    while read ip
	    do 
		 maquinas[$indice]=$ip
		 indice=$((indice + 1))
	    done < ips.txt

    	    for ip in ${maquinas[@]};
            do
		echo "$ip"
		ssh i0917867@$ip "bash ~/Documentos/trabajo_isis/TomcatServer/bin/catalina.sh stop; rm -f -R ~/Documentos/trabajo_isis"        
    	    done
	    ;;
        *)
            notverbose $@
            ;;
    esac
fi



