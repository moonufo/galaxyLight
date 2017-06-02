<template>
  <el-row>
    <el-form :inline="true" :model="formSreach" :rules="rules" ref="formSreach" class="demo-form-inline mt20 rxc">
      
        <el-form-item label="开始时间:" prop="taskBeginDate">
          <el-date-picker
            editable:false
            v-model="formSreach.taskBeginDate"
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开始时间">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="结束时间:" prop="taskEndDate">
          <el-date-picker
            editable:false
            v-model="formSreach.taskEndDate"
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="应用ID:" prop="taskSreachID">
          <el-input v-model="formSreach.taskSreachID" placeholder="请输入应用ID"></el-input>
        </el-form-item>

        <el-form-item label="应用名:">
          <el-input v-model="formSreach.taskSreachName" placeholder="请输入应用名"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="search" @click="onSubmit('formSreach')">查询</el-button>
          <el-button @click="resetForm('formSreach')">重置</el-button>
        </el-form-item>
    </el-form>
  </el-row>
</template>

<script>
import * as utils from './../utils' // utils 工具
export default {
  name: 'sreach',
  props: ['data'],
  data () {
    let checkID = (rule, value, callback) => { // 自定义校验方法，由form标签中rules规则调用
      if (value !== '' && !Number.isInteger(value * 1)) {
        callback(new Error('请输入数字值'));
      } else {
        callback();
      }
    }
    return {
      formSreach: {
        taskBeginDate: '', // 开始日期时间
        taskEndDate: '', // 结束日期时间
        taskSreachID: '', // 任务ID
        taskSreachName: '' // 任务名称
      },
      /* 校验规则集合，绑定在form标签 */
      rules: {
        taskSreachID: [ // 自定义校验方法
          {validator: checkID, trigger: 'blur'}
        ]
        // ,taskBeginDate: [ // 校验规则
        //   {required: false, message: ''},
        //   {type: 'date', message: '请重新选择开始日期', trigger: 'change'}
        // ],
        // taskEndDate: [ // 校验规则
        //   {required: false, message: ''},
        //   {type: 'date', message: '请重新选择结束日期', trigger: 'change'}
        // ]
      }
    }
  },
  methods: {
    onSubmit (obj) {
      this.$refs[obj].validate((pass) => {
        if (pass) {
          this.emit();
        } else {
          return false;
        }
      });
    },
    resetForm (obj) {
      this.$refs[obj].resetFields();
    },
    emit (obj) {
      this.formSreach.taskBeginDate = utils.formatDate(this.formSreach.taskBeginDate) // 开始标准时间 格式化为 yyyy-MM-dd HH:mm:ss
      this.formSreach.taskEndDate = utils.formatDate(this.formSreach.taskEndDate) // 结束标准时间 格式化为 yyyy-MM-dd HH:mm:ss
      // 传递数据至父组件，父组件进行数据搜索
      this.$emit('childMessage', this.formSreach)
    }
  }
}
</script>

<style>
</style>
