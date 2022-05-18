# killer-system
It is still developing.
## Known issues

- <s>A job that is impossible to be finished will stay in a dead loop.</s>
> Fixed: Added a break statement.
- <s>Shop.isOpen is not implemented, which will always return true.</s>
> Fixed: Implemented in Client but without cache.
- Class Manager is coupled with JobModel.
> Fixed: Coupling is not a shameful thing.
> 
## Todo
- [x] Make a job running state dashboard.
- [ ] Make a reporter connected with any platform like Dingtalk.
- [ ] Embed checker. (Need frontend support)
- [ ] Order cache.
- [ ] Arguments configuration.
- [x] Data source changed to MySQL.