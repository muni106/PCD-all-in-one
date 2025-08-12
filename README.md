PCD a.y. 2024-2025 - ISI LM UNIBO - Cesena Campus

# Single Assignment 

v1.0.0-20250606


L’assignment unico è articolato in due parti:
- Prima Parte - Programmazione concorrente
- Seconda Parte - Programmazione distribuita

### PRIMA PARTE - programmazione concorrente

- Realizzare un programma concorrente che, dato l'indirizzo di una directory D e una parola P,    fornisca il numero di file PDF presenti in D (o in una qualsiasi sottodirectory, ricorsivamente)  che contengono P. D e P sono parametri del programma, passati da linea di comando. Il programma non necessita di una GUI.

  Per l’elaborazione di file PDF nel repo del corso si usa la libreria PDFBox, con semplice esempio di utilizzo (``pcd.ass_single.part1.example.ExtractTextSimple``) in cui dato un documento PDF caricato da file se ne ottiene il testo corrispondente (come stringa).

- Estendere il programma al punto precedente includendo una GUI con:
  - Input box per specificare i parametri  
  - pulsanti start/stop/suspend/resume per avviare/fermare/sospendere/riprendere l'elaborazione
  - output box dove sia visualizzato man mano il numero di file analizzati (in totale) fino a quel momento, il numero di file PDF trovati e il numero di file PDF contenenti la parola P. 


Si chiede di sviluppare i due punti considerando in ordine sei approcci distinti, visti nel corso:

- **Soluzione basata su approccio a thread**
  - La soluzione in questo caso deve basarsi unicamente su un approccio basato su programmazione multithreaded, adottando, da un lato, principi e metodi di progettazione utili per favorire modularità, incapsulamento e proprietà relative, dall’altro una soluzione che massimizzi le performance e reattività. 
  - Come meccanismi di coordinazione prediligere la definizione e uso di monitor  rispetto ad altre soluzioni di più basso livello.
  - Non è consentito l’uso di librerie e classi esterne relative alla gestione degli aspetti di concorrenza: in particolare, relativamente alla libreria java.util.concurrent, è possibile utilizzare (eventualmente) solo le classi utili all’implementazione di monitor. 
- **Soluzione basata su approccio a virtual threads** 
  - La soluzione in questo caso deve sfruttare i virtual thread (in Java)
- **Soluzione basata su approccio a task**
  - La soluzione in questo caso deve sfruttare un approccio a task, usando come framework di riferimento, gli Executor in Java. 
- **Soluzione basata su programmazione asincrona ad eventi**
  - La soluzione in questo caso deve basarsi su programmazione asincrona ad eventi, basata su architettura di controllo ad event loop, usando un qualsiasi framework a supporto che implementi l'approccio.  Esempio di riferimento visto nel corso: Vert.x, in Java o nel linguaggio che si preferisce. E' possibile usare framework alternativi (es. Node.js in Javascript).  
- **Soluzione basata su programmazione reattiva**
  - La soluzione in questo caso deve basarsi sulla programmazione reattiva, sfruttando il framework Rx (RxJava se si usa Java come linguaggio di riferimento, non obbligatorio). 
- **Soluzione basata su attori** 
  - La soluzione in questo caso deve basarsi unicamente su un approccio ad attori, usando il framework Akka

### SECONDA PARTE - programmazione distribuita

Si vuole realizzare il prototipo di un'applicazione distribuita di "Cooperative Pixel Art" (presente nel repo, in versione concentrata). L'applicazione deve permettere a più utenti di condividere, visualizzare e modificare collaborativamente una griglia che rappresenta un'immagine in pixel.    Nel repo è disponibile un esempio (parziale) di versione centralizzata, a singolo utente (``pcd.ass_single.part2.example.PixelArtMain``).

In particolare:
- ogni utente può modificare l'immagine selezionando ("colorando") mediante il puntatore del mouse gli elementi della griglia (come mostrato nell'esempio fornito)
- le variazioni apportate da un utente devono essere opportunamente visualizzate anche dagli altri utenti, in  modo che tutti gli utenti vedano sempre il medesimo stato della griglia, in modo consistente.   In particolare:
  - se un utente visualizza la griglia allo stato s, ogni altro utente deve aver visualizzato o visualizzare lo stato s
  - se ev1 e ev2 sono due eventi che concernono la griglia per cui ev1 →  ev2  per un utente ui, allora ev1 →  ev2  per ogni altro utente uj
- gli utenti devono potersi aggiungere (e uscire)  dinamicamente, contattando uno qualsiasi degli utenti che già partecipano all'applicazione, in modo peer-to-peer. Nel caso si adotti un approccio basato su MOM, è possibile considerare – come punto di contatto – il nodo dove c'è il broker (o uno dei nodi dove c'è il broker, nel caso si considerino MOM che supportano forme di clustering/federazione)
- ogni utente deve poter percepire dove si trova il puntatore del mouse di tutti gli altri utenti che stanno collaborando

Si richiede di sviluppare due soluzioni:
- una soluzione utilizzando un approccio basato su scambio di messaggi, attori distribuiti oppure MOM. 
- una soluzione utilizzando un approccio basato su Java RMI come esempio di tecnologia che supporta Distributed Object Computing.

### DETTAGLI SULLA CONSEGNA

La consegna consiste in una directory ``Single-Assignment-Single`` compressa (formato zip) contenente due sotto-directory (``part1``, ``part2``), una per ogni parte. Ogni sottodirectory deve essere strutturata come segue:
- directory src con i sorgenti del programma
- directory doc che contenga una sintetica relazione in PDF (report.pdf)

La relazione deve includere:
- Analisi del problema, focalizzando in particolare gli aspetti relativi alla concorrenza
- Descrizione della strategia risolutiva e dell’architettura proposta, sempre focalizzando l’attenzione su aspetti relativi alla concorrenza.
  - da notare che - nella prima parte - la strategia risolutiva e architettura potrebbero variare a seconda dell'approccio utilizzato  
- (Quando ritenuto utile) La descrizione del comportamento del sistema o di sottoparti utilizzando Reti di Petri. 
- Prove di performance e considerazioni relative.
- Per la prima parte - in merito al punto 1) in particolare (soluzione a thread) - l'identificazione di proprietà di correttezza e verifica in JPF.

