Autors: Marc Serrajordi niub:20345522
        Hector Herranz  niub:20345651

Com executar el codi?

Obrirem dues consoles i executarem client-1.0-SNAPSHOT-jar-with-dependencies.jar i server-1.0-jar-with-dependencies.jar.
A partir d'aquí, executarem la següent instrucció al servidor: java -jar server-1.0-jar-with-dependencies -p 1234. Un cop executem aquesta comanda iens
sortirà el missatge de:
"Esperant una conexió d'un client". Després, executarem l'instrucció: java -jar client-1.0-SNAPSHOT-jar-with-dependencies -s localhost -p 1234.
LLavors, al servidor veurem el següent missatge:"Connexió acceptada d'un client" i "Esperant una connexió d'un client", ja que espera alguna acció del client.

A client, veiem que ens pregunta: "Vols generar una sessió nova (0) o recuperar una anterior (1)?".
Si volem fer una nova posem 0 i ens preguntarà : "Quin és el teu nom?" . Quan contestem, ens imprimirà la nostra sessió id perque aixi podrem recuperar la partida
més tard.Ens preguntarà "Amb quina paraula de 5 lletres vols jugar?" i li hem de contestar amb una paraula de 5 lletres.
Si la paraula existeix i és de 5 lletres ens retornarà un resultat on "*" vol dir que aquella lletra no està a la paraula,
"?" vol dir que aquella lletra està a la paraula però no en aquella posició i finalment "^" si aquella lletra està en aquella posició.
Després de 6 intents o quan haguem endevinat la paraula és printegen les estadístiques. Posteriorment et pregunta si vols continuar jugant
o no.

Si volem recuperar una sessió anterior, ens preguntarà la sessió i el nom d'aquella partida anterior.A partir d'aquí tornarà a
preguntar-nos amb quina paraula volem jugar.

Tots els errors que puguin succeir a l'hora de jugar manualment s'imprimeix un missatge per pantalla on et diu que has de fer
o que has fet malament. En alguns et deixa tornar a escriure si l'error és a causa de l'escriptura.


Com funciona el joc?

Tenim una classe "Client" la qual és l'encarregada d'obrir i tancar les connexions amb el servidor i d'iniciar el joc a través d'un mètode present a la classe game que serà la que
contindrà tota la lògica del joc. A partir d'aquí, quan nosaltres contestem les dues primeres preguntes que he explicat avans,
el client envia un missatge de "hello" el qual rep el servidor i si no hi ha cap error retorna que està "ready" per jugar.

El client llavors envia "Play" el qual "pregunta" al servidor si pot jugar. El servidor retorna "Admit" si és que sí.

Després, quan client ens pregunta la paraula, si es valida, client l'envia a servidor. Servidor a través d'un mètode retorna
el resultat comparant la paraula que hem enviat amb la paraula que ell ha escollit que és la que estem buscant.

Tant quan hem fet 6 intents com quan hem guanyat, servidor envia les estadístiques i client les llegeix i les imprimeix per pantalla.
Després pregunta si vols seguir jugant i si es així tornem a enviar el missatge "Play".

Com es distrubueix el codi?

Tant client com servidor tenen un protocol el qual es l'encarregat de llegir i enviar les diferents missatges explicats abans.

Les classes Client i Server només obren i tanquen connexions i inicien la lògica del joc.

Les classes Game i GameServidor són les que permeten jugar i que l'execució sigui com l'explicada anteriorment.
