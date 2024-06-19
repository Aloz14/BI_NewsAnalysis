@echo off
echo 日志开始生成

:: 激活虚拟环境
call ./.venv/Scripts/activate

python ./TimeSimulation.py

deactivate
