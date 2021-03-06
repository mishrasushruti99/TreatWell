<%-- 
    Document   : addDoctorClinic
    Created on : Oct 23, 2017, 5:24:39 PM
    Author     : Cori 5
--%>

<%@include file="../header.jsp"%>
<link rel="stylesheet" type="text/css" href="assets/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css"/>
<script type="text/javascript" src="assets/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js"></script>
<div class="page-head">
    <!-- BEGIN PAGE TITLE -->
    <div class="page-title">
        <h1>Doctor's Clinics</h1>
    </div>
</div>
<input type="hidden" id="can_edit" value="${requestScope.refData.CAN_EDIT}">
<input type="hidden" id="can_delete" value="${requestScope.refData.CAN_DELETE}">
<div class="modal fade" id="addDoctorClinic">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h3 class="modal-title">Assign Clinic</h3>
            </div>
            <div class="modal-body">
                <input type="hidden" id="doctorClinicId" value="">
                <form action="#" role="form" method="post" >
                    <div class="row" id="clinicNameRow">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label>Clinic Name</label>
                                <select class="form-control select2_category" data-placeholder="Choose a Clinic">       
                                    <option value="">Select Clinic</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Clinic Time From</label>
                                <div class="input-group bootstrap-timepicker  timepicker input-medium" id="timeFromPicker">
                                    <input id="timeFrom" type="text" class="form-control input-medium" readonly="">
                                    <span class="input-group-addon"><i class="glyphicon glyphicon-time"></i></span>
                                </div>
                            </div>
                        </div> 
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>To</label>
                                <div class="input-group bootstrap-timepicker timepicker input-medium" id="timeToPicker">
                                    <input id="timeTo" type="text" class="form-control input-medium" readonly="">
                                    <span class="input-group-addon"><i class="glyphicon glyphicon-time"></i></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Max Appointment</label>
                                <div class="input-group">
                                    <input id="maxAppointment" type="text" class="form-control" onkeyup="onlyInteger(this);">
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-2">
                            <div class="form-group">
                                <input id="mon" value="MON" name="weekDays" type="checkbox" class="icheck">
                                <label for="mon">MON</label>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">

                                <input id="tue" value="TUE" name="weekDays" type="checkbox" class="icheck">
                                <label>TUE</label>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">

                                <input id="wed" value="WED" name="weekDays" type="checkbox" class="icheck">
                                <label for="wed">WED</label>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">

                                <input id="thu" value="THU" name="weekDays" type="checkbox" class="icheck">
                                <label for="thur">THU</label>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">

                                <input id="fri" value="FRI" name="weekDays" type="checkbox" class="icheck">
                                <label for="fri">FRI</label>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <input id="sat" value="SAT" name="weekDays" type="checkbox" class="icheck">
                                <label for="sat">SAT</label>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <input id="sun" value="SUN" name="weekDays" type="checkbox" class="icheck">
                                <label for="sun">SUN</label>
                            </div>
                        </div>

                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="saveData();">Save</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        <div class="portlet-body">
            <form name="doctorform" action="#" role="form" onsubmit="return false;" method="post">
                <div class="portlet box green">
                    <div class="portlet-title">
                        <div class="caption">
                            Assign Clinics
                        </div>
                    </div>
                    <div class="portlet-body">
                        <input id="clinicId" type="hidden" value="">
                        <input id="doctorId" type="hidden" value="${requestScope.refData.doctorId}">
                        <div class="row">
                            <div class="col-md-12">
                                <div id="displayDiv">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    $(function () {
//        $('#clinicId').select2({
//            placeholder: "Select an option",
//            allowClear: true
//        });
        $('.icheck').iCheck({
            checkboxClass: 'icheckbox_square-green',
            increaseArea: '20%' // optional
        });
        $('#timeFrom').timepicker({minuteStep: 5, showMeridian: false});
        $('#timeTo').timepicker({minuteStep: 5, showMeridian: false});
        displayData();
    });
    function addClinicDialog() {
        $('#clinicId').find('option').remove();
        $.get('setup.htm?action=getAvailableClinicForDoctors', {doctorId: $('#doctorId').val()},
                function (list) {
                    if (list !== null && list.length > 0) {
                        for (var i = 0; i < list.length; i++) {
                            var newOption = new Option(list[i].CLINIC_NME, list[i].TW_CLINIC_ID, true, false);
                            $('#clinicId').append(newOption);
                        }
                        $('#addClinicDialog').modal('show');
                    } else {
                        $.bootstrapGrowl("No any clinic available yet. Please add clinic first.", {
                            ele: 'body',
                            type: 'info',
                            offset: {from: 'top', amount: 80},
                            align: 'right',
                            allow_dismiss: true,
                            stackup_spacing: 10
                        });
                    }
                    $('#clinicId').trigger('change');
                }, 'json');
        $('#clinicNameRow').show();
        $('#remarks').val('');
        $('#doctorClinicId').val('');
        $('#addDoctorClinic').modal('show');
    }
    function saveData() {

        var obj = {
            doctorClinicId: $('#doctorClinicId').val(), clinicId: $('#clinicId').val(), doctorId: $('#doctorId').val(),
            timeFrom: $('#timeFrom').val(), timeTo: $('#timeTo').val(), remarks: $('#remarks').val(),
            maxAppointment: $('#maxAppointment').val(), 'weekdaysarr[]': $("input[name='weekDays']:checked").getCheckboxVal()
        };

        $.post('setup.htm?action=saveDoctorClinic', obj, function (obj) {
            if (obj.result === 'save_success') {
                $.bootstrapGrowl("Clinic Data saved successfully.", {
                    ele: 'body',
                    type: 'success',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });
                $('input:text').val('');
                $('#remarks').val('');
                $('#clinicId').val('');
                $('#doctorClinicId').val();
                $('#addDoctorClinic').modal('hide');
                displayData();
                return false;
            } else {
                $.bootstrapGrowl("Error in saving Clinic. Please try again later.", {
                    ele: 'body',
                    type: 'danger',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });

                return false;
            }
        }, 'json');
        return false;
    }
    function deleteRow(id,clinicId,doctorId) {
        bootbox.confirm({
            message: "Do you want to delete record?",
            buttons: {
                confirm: {
                    label: 'Yes',
                    className: 'btn-success'
                },
                cancel: {
                    label: 'No',
                    className: 'btn-danger'
                }
            },
            callback: function (result) {
                if (result) {
                    $.post('setup.htm?action=deleteDoctorClinic', {id: id,clinicId:clinicId,doctorId:doctorId}, function (res) {
                        if (res.result === 'save_success') {
                            $.bootstrapGrowl("Record deleted successfully.", {
                                ele: 'body',
                                type: 'success',
                                offset: {from: 'top', amount: 80},
                                align: 'right',
                                allow_dismiss: true,
                                stackup_spacing: 10
                            });
                            displayData();
                        } else {
                            $.bootstrapGrowl("Record can not be deleted.", {
                                ele: 'body',
                                type: 'danger',
                                offset: {from: 'top', amount: 80},
                                align: 'right',
                                allow_dismiss: true,
                                stackup_spacing: 10
                            });
                        }
                    }, 'json');

                }
            }
        });
    }
    function displayData() {
        var $tbl = $('<table class="table table-striped table-bordered table-hover">');
        $tbl.append($('<thead>').append($('<tr>').append(
                $('<th class="center" width="5%">').html('Sr. #'),
                $('<th class="center" width="40%">').html('Clinic Name'),
                $('<th class="center" width="20%">').html('Timings'),
                $('<th class="center" width="20%">').html('Max Appointments'),
                $('<th class="center" width="15%">').html('&nbsp;')
                )));
        $.get('setup.htm?action=getClinicForDoctors', {doctorId: $('#doctorId').val()},
                function (list) {
                    if (list !== null && list.length > 0) {
                        $tbl.append($('<tbody>'));
                        for (var i = 0; i < list.length; i++) {
                            var editHtm = '<i class="fa fa-pencil-square-o" aria-hidden="true" title="Click to Edit" style="cursor: pointer;" onclick="editRow(\'' + list[i].TW_DOCTOR_CLINIC_ID + '\',\'' + list[i].TW_CLINIC_ID + '\');"></i>';
                            var delHtm = '<i class="fa fa-trash-o" aria-hidden="true" title="Click to Delete" style="cursor: pointer;" onclick="deleteRow(\'' + list[i].TW_DOCTOR_CLINIC_ID + '\',\'' + list[i].TW_CLINIC_ID + '\',\'' + list[i].TW_DOCTOR_ID + '\');"></i>';
                            if ($('#can_edit').val() !== 'Y') {
                                editHtm = '&nbsp;';
                            }
                            if ($('#can_delete').val() !== 'Y') {
                                delHtm = '&nbsp;';
                            }
                            $tbl.append(
                                    $('<tr>').append(
                                    $('<td  align="center">').html(eval(i + 1)),
                                    $('<td>').html(list[i].CLINIC_NME),
                                    $('<td>').html(list[i].TIME_FROM + ' - ' + list[i].TIME_TO),
                                    $('<td align="center">').html(list[i].TOTAL_APPOINTMENT),
                                    $('<td align="center">').html(editHtm)
                                    ));
                        }
                        $('#displayDiv').html('');
                        $('#displayDiv').append($tbl);
                        return false;
                    } else {
                        $('#displayDiv').html('');
                        $tbl.append(
                                $('<tr>').append(
                                $('<td  colspan="5">').html('<b>No data found.</b>')
                                ));
                        $('#displayDiv').append($tbl);
                        return false;
                    }
                }, 'json');
    }


    function editRow(id, clinicId) {
        $('#doctorClinicId').val(id);
        $('#clinicNameRow').hide();
        $('#clinicId').val(clinicId);

        $.get('setup.htm?action=getDoctorClinicById', {doctorClinicId: id},
                function (obj) {
                    //  $('#clinicId').val(obj.TW_CLINIC_ID);
                    $('#timeFrom').val(obj.TIME_FROM);
                    $('#timeTo').val(obj.TIME_TO);
                    $('#remarks').val(obj.REMARKS);
                    $('#maxAppointment').val(obj.TOTAL_APPOINTMENT);
                    if (obj.WEEK_DAY === '') {
                        $(".icheck").each(function (i, o) {
                            $(o).iCheck('check');
                        });
                    } else {
                        $('input:checkbox[name="weekDays"]').iCheck('uncheck');
                        var weekdays = obj.WEEK_DAY;
                        var days = weekdays.split(',');
                        for (var i = 0; i < $(days).length; i++) {
                            $('input:checkbox[name="weekDays"][value="' + days[i] + '"]').iCheck('check');
                        }
                    }
                    $('#addDoctorClinic').modal('show');
                }, 'json');
    }
    jQuery.fn.getCheckboxVal = function () {
        var vals = [];
        var i = 0;
        this.each(function () {
            vals[i++] = jQuery(this).val();
        });
        return vals;
    };
</script>

<%@include file="../footer.jsp"%>




