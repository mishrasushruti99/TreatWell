<%-- 
    Document   : addCity
    Created on : Nov 20, 2017, 8:48:03 PM
    Author     : Cori 5
--%>

<%@include file="../header.jsp"%>
<script>
    $(function () {
        displayData();
    });
    function displayData() {
        var $tbl = $('<table class="table table-striped table-bordered table-hover">');
        $tbl.append($('<thead>').append($('<tr>').append(
                $('<th class="center" width="5%">').html('Sr. #'),
                $('<th class="center" width="30%">').html('City Name'),
                $('<th class="center" width="20%">').html('Country Name'),
                $('<th class="center" width="15%" colspan="2">').html('&nbsp;')
                )));
        $.get('clinic.htm?action=getCity', {cityName: $('#cityName').val()},
                function (list) {
                    if (list !== null && list.length > 0) {
                        $tbl.append($('<tbody>'));
                        for (var i = 0; i < list.length; i++) {
                            var editHtm = '<i class="fa fa-pencil-square-o" aria-hidden="true" title="Click to Edit" style="cursor: pointer;" onclick="editRow(\'' + list[i].CITY_ID + '\');"></i>';
                            var delHtm = '<i class="fa fa-trash-o" aria-hidden="true" title="Click to Delete" style="cursor: pointer;" onclick="deleteRow(\'' + list[i].CITY_ID + '\');"></i>';
                            if ($('#can_edit').val() !== 'Y') {
                                editHtm = '&nbsp;';
                            }
                            if ($('#can_delete').val() !== 'Y') {
                                delHtm = '&nbsp;';
                            }
                            $tbl.append(
                                    $('<tr>').append(
                                    $('<td  align="center">').html(eval(i + 1)),
                                    $('<td>').html(list[i].CITY_NME),
                                    $('<td>').html(list[i].COUNTRY_NME),
                                    $('<td align="center">').html(editHtm),
                                    $('<td  align="center">').html(delHtm)
                                    ));
                        }
                        $('#displayDiv').html('');
                        $('#displayDiv').append($tbl);
                        return false;
                    } else {
                        $('#displayDiv').html('');
                        $tbl.append(
                                $('<tr>').append(
                                $('<td  colspan="7">').html('<b>No data found.</b>')
                                ));
                        $('#displayDiv').append($tbl);
                        return false;
                    }
                }, 'json');
    }

    function saveData() {
        if ($.trim($('#cityName').val()) === '') {
            $('#cityName').notify('City Name is Required Field', 'error', {autoHideDelay: 15000});
            $('#cityName').focus();
            return false;
        }
        var obj = {
            cityId: $('#cityId').val(),
            cityName: $('#cityName').val(),
            countryId: $('#countryId').val(),
            provinceId: $('#provinceId').val()
        };
        $.post('clinic.htm?action=saveCity', obj, function (obj) {
            if (obj.result === 'save_success') {
                $.bootstrapGrowl("City Data saved successfully.", {
                    ele: 'body',
                    type: 'success',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });
                $('input:text').val('');
                $('#cityId').val('');
                $('#addCity').modal('hide');
                displayData();
                return false;
            } else {
                $.bootstrapGrowl("Error in saving City. Please try again later.", {
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

    function deleteRow(id) {
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
                    $.post('clinic.htm?action=deleteCity', {id: id}, function (res) {
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

    function addCityDialog() {
        $('#cityId').val('');
        $('#cityName').val('');
        $('#countryId').find('option:first').attr('selected', 'selected');
         $('#provinceId').find('option:first').attr('selected', 'selected');
        $('#addCity').modal('show');
    }
    function editRow(id) {
        $('#cityId').val(id);
        $.get('clinic.htm?action=getCityById', {cityId: id},
                function (obj) {
                    $('#cityName').val(obj.CITY_NME);
                    $('#countryId').val(obj.COUNTRY_ID);
                    $('#provinceId').val(obj.PROVINCE_ID);
                    $('#addCity').modal('show');
                }, 'json');
    }

</script>
<div class="page-head">
    <!-- BEGIN PAGE TITLE -->
    <div class="page-title">
        <h1>City</h1>
    </div>
</div>
<div class="modal fade" id="addCity">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h3 class="modal-title">Add City</h3>

            </div>
            <div class="modal-body">
                <input type="hidden" id="cityId" value="">
                <form action="#" role="form" method="post" >
                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label>City Name *</label>
                                <div>
                                    <input type="text" class="form-control" id="cityName" placeholder="City Name" >
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Country</label>
                                <select id="countryId" class="form-control">
                                    <c:forEach items="${requestScope.refData.countries}" var="obj">
                                        <option value="${obj.COUNTRY_ID}">${obj.COUNTRY_NME}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div> 
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Province</label>
                                <select id="provinceId" class="form-control">
                                    <c:forEach items="${requestScope.refData.province}" var="obj">
                                        <option value="${obj.PROVINCE_ID}">${obj.PROVINCE_NME}</option>
                                    </c:forEach>
                                </select>
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
        <div class="portlet box green">
            <div class="portlet-title tabbable-line">
                <div class="caption">
                    City
                </div>
            </div>
            <div class="portlet-body">
                <input type="hidden" id="can_edit" value="${requestScope.refData.CAN_EDIT}">
                <input type="hidden" id="can_delete" value="${requestScope.refData.CAN_DELETE}">
                <form action="#" onsubmit="return false;" role="form" method="post">
                    <div class="row">
                        <div class="col-md-12 text-right" style="padding-top: 23px;">
                            <c:if test="${requestScope.refData.CAN_ADD=='Y'}">
                                <button type="button" class="btn blue" onclick="addCityDialog();"><i class="fa fa-plus-circle"></i> New City</button>
                            </c:if>
                        </div>
                    </div>
                    <br/>
                    <div class="row">
                        <div class="col-md-12">
                            <div id="displayDiv"></div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@include file="../footer.jsp"%>


