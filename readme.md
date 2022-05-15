# killer-system
It is still developing.
## Known issues

- A job that is impossible to be finished will stay in a dead loop.
> Fix method: Add a timeout param.
- Shop.isOpen is not implemented, which will always return true.
> Fix method: Import redis and cache it.
- Class Manager is coupled with JobModel.
> Fix method: Rewrite Manager.runJob as a standalone function.
> 
## Todo
- Make a job running state dashboard.
- Make a reporter connected with any platform like Dingtalk.
- Embed checker. (Need frontend support)
- Order cache.
- Arguments configuration.
- Data source.