let
  sources = import ./nix/sources.nix;
  frameworks = nixpkgs.darwin.apple_sdk.frameworks;

  nixpkgs = import sources.nixpkgs { overlays = []; };
in
  with nixpkgs;
stdenv.mkDerivation {
  name = "m2x-nuxt-shell";
  shellHook = ''
    export JAVA_HOME="${pkgs.jdk17}/zulu-17.jdk/Contents/Home"
  '';
  buildInputs = [
    # General
    # niv
    git-lfs
    google-cloud-sdk
    hexyl
    hyperfine
    jq
    pixman
    pwgen
    tokei
    vagrant
    yq 

    # Node/TSC Specifics
    nodePackages.javascript-typescript-langserver
    nodePackages.node2nix
    nodePackages.typescript
    nodePackages.npm
    nodePackages.firebase-tools
    nodejs-16_x
    yarn
    deno
    jdk17
    gradle
  ]
  ++ lib.optional stdenv.isLinux [
  ]
  ++ lib.optional stdenv.isDarwin [
    frameworks.Security
    frameworks.CoreFoundation
    frameworks.CoreServices
  ];
}
