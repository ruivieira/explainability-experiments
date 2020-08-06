# Counterfactuals

## Using OptaPlanner

### Using a `HardSoftScore`

With the `HardSoftScore` version, the solution is calculated by penalising the *hard score* when an input constraint is not met and by penalising the *soft score* as function of the Manhattan distance between the input and original features, and the distance between the prediction and desired outcome. 

![](../docs/hardsoftscore.png)