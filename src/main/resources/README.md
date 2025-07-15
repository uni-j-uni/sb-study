## Main Repository Clone 과정에서 서브 모듈까지 Clone

```bash
git clone {submodule repository}
git submodule init
git submodule update
```
또는
```bash
git clone --recurse-submodules {submodule repository}
```

## 서브 모듈 PUSH

(Root Repository에서)
```bash
cd {submodule repository}
git checkout main
git add .
git commit -m "🔧Settings: ~~~"
git push
```

## 서브 모듈 최신화
### PULL
(Root Repository에서)
```bash
git submodule update --remote --merge
```

### PUSH
서브 모듈에 수정사항이 생겼을 경우 메인 모듈에서 커밋을 같이 업데이트

(Root Repository에서)
```bash
git push --recurse-submodules=check
```
또는
```bash
git push --recurse-submodules=on-demand
```
check: 서브 모듈이 push 되어있는지 확인하는 옵션 (체크 결과에 따라 push 성공/실패가 정해짐)

on-demand: 서브 모듈이 push 되어있지 않다면 Git이 push를 대신 시도해주는 옵션
