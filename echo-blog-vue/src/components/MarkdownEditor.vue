<template>
  <div class="markdown-editor">
    <div class="toolbar">
      <el-button-group>
        <el-button @click="insertText('# ')" title="标题1">H1</el-button>
        <el-button @click="insertText('## ')" title="标题2">H2</el-button>
        <el-button @click="insertText('### ')" title="标题3">H3</el-button>
        <el-button @click="insertText('**粗体**')" title="粗体"><i class="el-icon-bold">B</i></el-button>
        <el-button @click="insertText('*斜体*')" title="斜体"><i class="el-icon-italic">I</i></el-button>
        <el-button @click="insertText('[链接文字](https://example.com)')" title="链接">链接</el-button>
        <el-button @click="insertText('![图片描述](https://example.com/image.jpg)')" title="图片">图片</el-button>
        <el-button @click="insertText('- 列表项\n- 列表项')" title="无序列表">列表</el-button>
        <el-button @click="insertText('1. 列表项\n2. 列表项')" title="有序列表">数字列表</el-button>
        <el-button @click="insertText('> 引用文字')" title="引用">引用</el-button>
        <el-button @click="insertText('```\n代码块\n```')" title="代码块">代码块</el-button>
        <el-button @click="insertText('---')" title="分割线">分割线</el-button>
      </el-button-group>
      <el-switch
        v-model="previewMode"
        active-text="预览"
        inactive-text="编辑"
        class="preview-switch"
      />
    </div>
    
    <div class="editor-container">
      <div v-if="!previewMode" class="editor-area">
        <el-input
          v-model="content"
          type="textarea"
          :rows="20"
          placeholder="请输入Markdown内容..."
          @input="updateContent"
        />
      </div>
      <div v-else class="preview-area">
        <div class="markdown-body" v-html="renderedContent"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue';
import { marked } from 'marked';

export default {
  name: 'MarkdownEditor',
  props: {
    modelValue: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue', 'html-content'],
  setup(props, { emit }) {
    const content = ref(props.modelValue);
    const previewMode = ref(false);
    
    const renderedContent = computed(() => {
      return marked(content.value || '');
    });
    
    watch(() => props.modelValue, (newValue) => {
      content.value = newValue;
    });
    
    const updateContent = (val) => {
      emit('update:modelValue', val);
      emit('html-content', renderedContent.value);
    };
    
    const insertText = (text) => {
      const textarea = document.querySelector('.editor-area textarea');
      if (!textarea) return;
      
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      const oldContent = content.value || '';
      
      // 插入文本
      content.value = oldContent.substring(0, start) + text + oldContent.substring(end);
      
      // 更新光标位置
      setTimeout(() => {
        textarea.focus();
        textarea.selectionStart = start + text.length;
        textarea.selectionEnd = start + text.length;
      }, 0);
      
      updateContent(content.value);
    };
    
    return {
      content,
      previewMode,
      renderedContent,
      updateContent,
      insertText
    };
  }
};
</script>

<style scoped>
.markdown-editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.toolbar {
  padding: 8px;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor-container {
  min-height: 400px;
}

.editor-area {
  height: 100%;
}

.preview-area {
  padding: 16px;
  min-height: 400px;
  overflow-y: auto;
}

.markdown-body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
  font-size: 16px;
  line-height: 1.5;
  word-wrap: break-word;
}

.markdown-body h1,
.markdown-body h2 {
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-body pre {
  background-color: #f6f8fa;
  border-radius: 3px;
  padding: 16px;
  overflow: auto;
}

.markdown-body code {
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  padding: 0.2em 0.4em;
}

.markdown-body blockquote {
  border-left: 0.25em solid #dfe2e5;
  color: #6a737d;
  padding: 0 1em;
  margin: 0 0 16px 0;
}

.preview-switch {
  margin-left: auto;
}
</style> 