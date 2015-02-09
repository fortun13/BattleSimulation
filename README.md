BattleSimulation
================

Medieval battle simulation done using Multi-Agent System


Making it work:
Add jade.jar to build config in your IDE
Create run configuration like this:
  main method class: jade.Boot
  program arguments: -gui server:main.java.agents.ServerAgent
  
(and now it should work)

Running: 
```
  java -jar BattleSimulation.jar -local-port 30123 server:main.java.agents.ServerAgent
```

