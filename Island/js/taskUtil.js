function oc(a)
{
	var o = {};
	for(var i=0;i<a.length;i++)
	{
		o[a[i]]='';
	}
	return o;
}
function task(id, name, categories) {
	this.name=name;
	this.id=id;
	this.categories=categories;
    this.listview=$("<li id=\""+this.id+"\">"+name+"</li>");
    this.ganttView=$("<div></div>");
//    this.listview=$();
//    this.elemview=$();
}
function person(id, name, teams) {
	this.name=name;
	this.id=id;
	this.teams=teams;
    	this.listview=$('<li id=\"'+this.id+'\">'+name+'</li>');
        
}
function removeFromArray(object, array) {
	var idx = array.indexOf(object); // Find the index
	if(idx!=-1) array.splice(idx, 1); // Remove it if really found!
	return array;
}


	var n=1;
    var p=1;
	function inc(){return n++;}
	function pinc(){return p++;}

	var tasks = new Object();
//var taskList = $("#tasklist");
var currentTask;
var category = $('');
var currentUsers;
var activeCategories;
var currentUser;
var activeTeams;
$(document).ready(function(){
  taskUtil();
				  personUtil();
  function taskUtil() {
    $("#closeTask").click(function(){
    $("#taskCurrent").hide(100);});
    $("#saveTask").click(function(){
      saveTask();});
    $("#addCategory").click(function(){
    $("#taskCurrent").hide(100, function() {$("#categoryCurrent").show(100);})});
	$("#addTask").click(function() {
      var taskList = $("#tasklist > ul");
      currentTask = new task(inc(), "New Task", "");
      currentTask.listview.click(function() {alert($(this).attr("id"));currentTask=tasks[$(this).attr("id")];updateTask();$("#categoryCurrent").hide(100, function() {$("#taskCurrent").show(100);});});
      tasks[currentTask.id] =currentTask;
      taskList.append(currentTask.listview);
      $("#ganttChart").append(currentTask.ganttView);
      //alert($("#endDate").val());
      updateTask();
      saveTask();
	  $("#categoryCurrent").hide(100, function() {$("#taskCurrent").show(100);})
    });
   }
				  function personUtil() {
				  $("#saveUser").click(function(){
									   saveUser();
/*                                       currentUser.name=$("#newUser");
									   currentUser.teams=activeTeams;
									   $("#currentUser").hide(100, function(){});*/});
				  $("#addUser").click(function(){	
                                    var userList = $("#userList ul");								  currentUser = new person(pinc(), "New Person", "");updateUser(); userList.append(currentUser.listview);

										  $("#currentTeam").hide(100, function() {$("#currentUser").show(100);})});
				  $("#addTeam").click(function(){
									  $("#currentUser").hide(100, function() {$("#currentTeam").show(100);})});
				  
				  }
				  
});
function updateUser() {
$("#newUser").val(currentUser.name);

}
function saveUser() {
currentUser.name=$("#newUser").val();
currentUser.listview.html(currentUser.name);
}
function updatecurrentUsers(user) {
	if(!(user in oc(currentUsers))) {
	   currentUsers.append(user);
	}
}
	   function removecurrentUsers(user) {
	   if(user in oc(currentUsers)) {
		   removeFromArray(user,currentUsers);
		  }
		  }
function updateTask() {
//alert(taskCurrent);
$("#newTask").val(currentTask.name);
}
var colors = ["#F5C049","#3EFF53","#FF0DE1","#5A49F5","#F54949"];
function saveTask() {
currentTask.name=$("#newTask").val();
currentTask.ganttView.css("background",colors[currentTask.id-1]);
currentTask.listview.html(currentTask.name);      currentTask.ganttView.css("width",""+calculateDate()); currentTask.ganttView.css("left",""+parseInt($("#startDate").val())+"px");currentTask.ganttView.css("top",""+((currentTask.id-1))*22+"px");

}
function calculateDate(startDate, endDate) {//alert($("#endDate").val());
return parseInt($("#endDate").val())-parseInt($("#startDate").val());//-
}