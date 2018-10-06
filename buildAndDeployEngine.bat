
pushd ".\Engine"

call gradle clean assemble

copy '.\build\lib\Engine.war' 'D:\GitHub\app\webapps'

popd