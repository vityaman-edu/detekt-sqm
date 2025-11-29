# Detekt Custom Rules: Software Quality Metrics

## Usage

```bash
gradle build
detekt --input ... --plugins build/libs/detekt-sqm-0.1.0.jar --all-rules
```

## Plan

- [ ] WMC: Weighted methods per class
- [ ] DIT: Depth of Inheritance Tree
- [ ] NOC: Number of Children
- [ ] CBO: Coupling between object classes
- [ ] RFC: Response for a Class
- [ ] LCOM: Lack of cohesion in methods
- [ ] Ca: Afferent couplings
- [ ] NPM: Number of public methods

## References
- https://github.com/yegor256/sqm
- https://www.aivosto.com/project/help/pm-oo-ck.html
- [S. R. Chidamber and C. F. Kemerer, "A metrics suite for object oriented design"](https://www.researchgate.net/profile/Chris-Kemerer/publication/3187649_Kemerer_CF_A_metric_suite_for_object_oriented_design_IEEE_Trans_Softw_Eng_206_476-493/links/564e30ca08ae4988a7a60877/Kemerer-CF-A-metric-suite-for-object-oriented-design-IEEE-Trans-Softw-Eng-206-476-493.pdf)
- https://detekt.dev/docs/introduction/extensions/
